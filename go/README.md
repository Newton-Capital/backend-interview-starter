# Go scaffold

Wiring only. `make smoke` compiles the service and applies the (empty)
migration to Postgres; `make run` starts it on port 8080 with `GET /healthz`.

| Where | What |
| --- | --- |
| `cmd/service` | the HTTP server; add your routes here |
| `cmd/migrate` | applies `migrations/*.sql` with golang-migrate |
| `cmd/seed` | parses `seed/*.json` and calls `internal/seed.Insert`, which you finish once your schema exists |
| `internal/config` | `PORT`, `DATABASE_URL`, `STUB_URL`, `SEED_DIR` from the environment |
| `internal/db` | a pgx pool |
| `migrations` | one empty migration; the schema is yours |

There is no client for the credit service. You write it.
