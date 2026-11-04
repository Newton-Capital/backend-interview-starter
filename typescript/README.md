# TypeScript scaffold

Wiring only. `make smoke` installs, type-checks and applies the (empty)
migration to Postgres; `make run` starts Fastify on port 8080 with `GET /healthz`.

| Where | What |
| --- | --- |
| `src/server.ts` | the Fastify server; add your routes here |
| `src/config.ts` | `PORT`, `DATABASE_URL`, `STUB_URL`, `SEED_DIR` from the environment |
| `src/db.ts` | a pg pool |
| `src/seed/` | parses `seed/*.json` and calls `insert`, which you finish once your schema exists |
| `migrations/` | one empty node-pg-migrate migration; the schema is yours |

There is no client for the credit service. You write it.
