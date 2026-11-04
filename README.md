# Send service: interview starter

This repository is the starting point for a 75-minute live session with
LemFi. You build a small money-movement service against a fixed HTTP
contract, in the language you choose from the four scaffolds here, on your
own machine, with your own editor and tools. Nothing is submitted in
advance; the session is the work.

Please finish the setup section before the session. It takes about
fifteen minutes and needs only Docker and your language's toolchain.

## What the service does

A customer asks for a **quote** for sending money, then **initiates a
payout** against that quote. The quote fixes the price; the payout moves
the funds.


### Rules

- **Money** is a decimal string with exactly two places (`"100.50"`), with
  a currency. Treat anything else as a validation error. Amounts are never
  floating point.
- **Wallet fee**: 1.5% of the amount, rounded half up to two places, with a
  minimum per currency (`seed/fees.json`). The quote total is amount plus
  fee.
- **A quote expires** 5 minutes after it is issued. A payout against an
  expired quote is `410 QUOTE_EXPIRED` and moves nothing.
- **Insufficient funds** is `422 INSUFFICIENT_FUNDS`, and a wallet never
  goes below zero, whatever arrives at the same time.
- **Ownership**: a wallet, quote or payout that belongs to another customer
  is `403 FORBIDDEN` for the caller, not `404`. The caller is the
  `X-User-Id` header; the gateway has already authenticated it.
- **Idempotency**: every `POST /payouts` carries an `Idempotency-Key`. The
  same key with the same body returns the original payout with `200`,
  however many times and however concurrently it arrives. The same key with
  a different body is `409 IDEMPOTENCY_CONFLICT`.

The contract is `specs/candidate-service.yaml`. The harness that checks
your service during the session treats it as a black box over HTTP, so the
contract is exact and the implementation is yours.

## Setup, before the session

You need Docker (with Compose) and the toolchain for your language: Go
1.22+, Node 22+, Java 21 with no Gradle install needed, or Python 3.12+.

```
cp .env.example .env        # set SCAFFOLD to go, java, typescript or python
make up                     # starts the partner stub and PostgreSQL
make smoke                  # builds your scaffold and runs its empty migration
```

`make smoke` ending in `<your scaffold>: ok` is the whole pre-session
check. If port 5432 is taken on your machine, set `POSTGRES_PORT` in
`.env`; everything reads it.

Each scaffold is wiring only: a health endpoint, configuration from the
environment, a database connection, one empty migration and a seed command
that reads `seed/*.json` and stops at a clearly marked function for you to
complete once your tables exist. Pick one and delete the other three if you
like. `make run` starts it on port 8080; `make seed` loads the seed data,
and must be safe to run again (it resets the data to the files).

## In the session

The session is a screen share with one interviewer. Roughly:

1. The interviewer gives you a link; `make harness-load URL=<link>` loads
   the check tool. From then on `make tier1` runs the public checks against
   your running service and writes a report to `out/`; `make report` opens it.
2. You build the wallet path: quotes, payouts, the seed, idempotency.
   `make tier1` is run around the 35-minute mark.
4. `make tier1` is run again around 50 minutes, then the interviewer runs
   further checks and talks through what they show, including what your
   service does when the partner misbehaves.
5. The last part is discussion: the decisions you made and the ones you
   would make with more time.

The public checks cover the fee rule and rounding, quote expiry,
ownership, insufficient funds, the two basic idempotency cases and the
happy paths. You can read them as the rules above: if the rules hold, they
pass.

Use whatever you normally use to write code, including AI tools. We are
interested in the service you end up with and in how you reason about it.
