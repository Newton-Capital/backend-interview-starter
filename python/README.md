# Python scaffold

Wiring only. `make smoke` creates a venv, installs, and applies the (empty)
Alembic migration to Postgres; `make run` starts FastAPI on port 8080 with `GET /healthz`.

| Where | What |
| --- | --- |
| `app/main.py` | the FastAPI app; add your routes here |
| `app/config.py` | `PORT`, `DATABASE_URL`, `STUB_URL`, `SEED_DIR` from the environment |
| `app/db.py` | a psycopg connection |
| `seed/` | parses `seed/*.json` and calls `insert`, which you finish once your schema exists (`python -m seed`) |
| `alembic/versions` | one empty migration; the schema is yours |

There is no client for the credit service. You write it.
