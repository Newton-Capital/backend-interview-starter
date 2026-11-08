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

A send is funded from the customer's wallet, which is debited by the quote
total. A payout is complete once the wallet is debited. There is no
downstream delivery to build.

The contract your service implements is
[`specs/candidate-service.yaml`](specs/candidate-service.yaml). The harness
that checks your service during the session treats it as a black box over
HTTP, so the contract is exact. Everything behind it is yours to design:
storage, data model, how you handle failures.

### Business rules

- All amounts in the API are decimal strings with the currency's usual
  precision, for example `"100.50"` for GBP. How you represent, store and
  calculate money inside your service is your decision.
- A quote is valid for 5 minutes. A payout must reference an unexpired
  quote.
- A wallet quote charges a fee of 1.5% of the amount, rounded half up, with
  a minimum per currency in `seed/fees.json`. The quote total is amount plus
  fee.
- A wallet and a quote belong to one customer. The customer is identified by
  the `X-User-Id` header, which you can trust: an upstream gateway sets it.

## Setup, before the session

You need Docker (with Compose) and the toolchain for your language: Go
1.24+, Node 22+, Java 21 with no Gradle install needed, or Python 3.12+.

```
cp .env.example .env        # set SCAFFOLD to go, java, typescript or python
make up                     # starts PostgreSQL and a partner container this task does not use
make smoke                  # builds your scaffold and runs its empty migration
```

`make smoke` ending in `<your scaffold>: ok` is the whole pre-session
check. If port 5432 is taken on your machine, set `POSTGRES_PORT` in
`.env`; everything reads it.

### If something fails

- `make smoke` stops with `dyld: missing LC_UUID load command` on macOS: your
  Go is older than the scaffold asks for. Its `go.mod` says Go 1.24, which a
  Go 1.21 or later install downloads on the first build; if yours does not,
  install Go 1.24 or later.
- `make tier1` cannot reach your service although `curl localhost:8080/healthz`
  answers: the check tool runs in a container and reaches your service on the
  host as `host.docker.internal`. Rancher Desktop names the host
  `host.lima.internal` instead; set `CANDIDATE_URL=http://host.lima.internal:8080`
  in `.env`. Any other Docker setup with its own name for the host takes it
  through the same variable.

Each scaffold is wiring only: a health endpoint, configuration from the
environment, a database connection, one empty migration and a seed command
that reads `seed/*.json` and stops at a clearly marked function for you to
complete once your tables exist. Pick one and delete the other three if you
like. `make run` starts it on port 8080; `make seed` loads the seed data,
and must be safe to run again (it resets the data to the files).

## In the session

The session is a screen share with one interviewer. Roughly:

1. `make harness-load` pulls the check tool at the version this repository
   pins; if the interviewer gives you an image or a link instead, pass it as
   `make harness-load URL=<image or link>`. From then on `make tier1` runs
   the public checks against your running service and writes a report to
   `out/`; `make report` opens it.
2. You build the wallet path: quotes, payouts, the seed. `make tier1` is run
   around the 35-minute mark.
3. `make tier1` is run again around 50 minutes, then the interviewer runs
   further checks and talks through what they show.
4. The last part is discussion: the decisions you made and the ones you
   would make with more time.

The public checks follow the business rules and the contract: if both hold,
they pass.

Use whatever you normally use to write code, including AI tools. We are
interested in the service you end up with and in how you reason about it.
