import { readFile } from "node:fs/promises";
import { join } from "node:path";

export interface Wallet {
  walletId: string;
  userId: string;
  currency: string;
  balance: string; // decimal string, two places
}

export interface Quote {
  quoteId: string;
  userId: string;
  walletId: string;
  fundingSource: string;
  amount: string;
  fee: string;
  total: string;
  expiresAt: string; // RFC 3339
}

export interface Fees {
  quoteTtlSeconds: number;
  wallet: { feePercent: string; minimumFee: Record<string, string>; rounding: string };
}

export interface SeedData {
  wallets: Wallet[];
  quotes: Quote[];
  fees: Fees;
}

const twoPlaces = /^[0-9]+\.[0-9]{2}$/;

/** Reads and validates seed/wallets.json and seed/fees.json. */
export async function loadSeed(dir: string): Promise<SeedData> {
  const wallets = JSON.parse(await readFile(join(dir, "wallets.json"), "utf8")) as { wallets: Wallet[]; quotes?: Quote[] };
  const fees = JSON.parse(await readFile(join(dir, "fees.json"), "utf8")) as Fees;
  const data: SeedData = { wallets: wallets.wallets ?? [], quotes: wallets.quotes ?? [], fees };
  validate(data);
  return data;
}

export function validate(d: SeedData): void {
  if (d.wallets.length === 0) throw new Error("wallets.json: no wallets");
  const seen = new Set<string>();
  for (const w of d.wallets) {
    if (!w.walletId || !w.userId || !w.currency) throw new Error(`wallet ${JSON.stringify(w)}: walletId, userId and currency are required`);
    if (!twoPlaces.test(w.balance)) throw new Error(`wallet ${w.walletId}: balance ${JSON.stringify(w.balance)} is not a two-place decimal string`);
    if (seen.has(w.walletId)) throw new Error(`wallet ${w.walletId} appears twice`);
    seen.add(w.walletId);
    if (!(w.currency in d.fees.wallet.minimumFee)) throw new Error(`fees.json has no minimum fee for ${w.currency} (wallet ${w.walletId})`);
  }
  for (const q of d.quotes) {
    if (!q.quoteId || !seen.has(q.walletId) || ![q.amount, q.fee, q.total].every((x) => twoPlaces.test(x)) || !q.expiresAt) {
      throw new Error(`quote ${JSON.stringify(q)} is incomplete or refers to an unknown wallet`);
    }
  }
  if (!(d.fees.quoteTtlSeconds > 0) || !d.fees.wallet.feePercent) throw new Error("fees.json: quoteTtlSeconds and wallet.feePercent are required");
}
