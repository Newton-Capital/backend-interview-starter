"""Reads and validates seed/wallets.json and seed/fees.json. Done; the insert is yours."""

import json
import re
from dataclasses import dataclass, field
from pathlib import Path

TWO_PLACES = re.compile(r"^[0-9]+\.[0-9]{2}$")


@dataclass(frozen=True)
class Wallet:
    wallet_id: str
    user_id: str
    currency: str
    balance: str  # decimal string, two places


@dataclass(frozen=True)
class Quote:
    quote_id: str
    user_id: str
    wallet_id: str
    funding_source: str
    amount: str
    fee: str
    total: str
    expires_at: str  # RFC 3339


@dataclass(frozen=True)
class Fees:
    quote_ttl_seconds: int
    fee_percent: str
    minimum_fee: dict[str, str]
    rounding: str


@dataclass(frozen=True)
class SeedData:
    wallets: list[Wallet]
    quotes: list[Quote]
    fees: Fees
    extra: dict = field(default_factory=dict)


def load(seed_dir: str) -> SeedData:
    d = Path(seed_dir)
    raw = json.loads((d / "wallets.json").read_text())
    fees_raw = json.loads((d / "fees.json").read_text())
    data = SeedData(
        wallets=[Wallet(w["walletId"], w["userId"], w["currency"], w["balance"]) for w in raw.get("wallets", [])],
        quotes=[
            Quote(q["quoteId"], q["userId"], q["walletId"], q["fundingSource"], q["amount"], q["fee"], q["total"], q["expiresAt"])
            for q in raw.get("quotes", [])
        ],
        fees=Fees(
            quote_ttl_seconds=int(fees_raw.get("quoteTtlSeconds", 0)),
            fee_percent=fees_raw.get("wallet", {}).get("feePercent", ""),
            minimum_fee=fees_raw.get("wallet", {}).get("minimumFee", {}),
            rounding=fees_raw.get("wallet", {}).get("rounding", ""),
        ),
    )
    validate(data)
    return data


def validate(d: SeedData) -> None:
    if not d.wallets:
        raise ValueError("wallets.json: no wallets")
    seen: set[str] = set()
    for w in d.wallets:
        if not (w.wallet_id and w.user_id and w.currency):
            raise ValueError(f"wallet {w}: walletId, userId and currency are required")
        if not TWO_PLACES.match(w.balance):
            raise ValueError(f"wallet {w.wallet_id}: balance {w.balance!r} is not a two-place decimal string")
        if w.wallet_id in seen:
            raise ValueError(f"wallet {w.wallet_id} appears twice")
        seen.add(w.wallet_id)
        if w.currency not in d.fees.minimum_fee:
            raise ValueError(f"fees.json has no minimum fee for {w.currency} (wallet {w.wallet_id})")
    for q in d.quotes:
        if not q.quote_id or q.wallet_id not in seen or not all(TWO_PLACES.match(x) for x in (q.amount, q.fee, q.total)) or not q.expires_at:
            raise ValueError(f"quote {q} is incomplete or refers to an unknown wallet")
    if d.fees.quote_ttl_seconds <= 0 or not d.fees.fee_percent:
        raise ValueError("fees.json: quoteTtlSeconds and wallet.feePercent are required")
