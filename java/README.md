# Java scaffold

Wiring only. `make smoke` compiles with Gradle and applies the (empty) Flyway
migration to Postgres; `make run` starts Spring Boot on port 8080 with `GET /healthz`.

| Where | What |
| --- | --- |
| `com.lemfi.send.Application` | Spring Boot entry point |
| `com.lemfi.send.HealthController` | `GET /healthz`; add your controllers beside it |
| `com.lemfi.send.AppConfig` | `PORT`, `DATABASE_URL`, `STUB_URL`, `SEED_DIR` from the environment; builds the `DataSource` |
| `com.lemfi.send.MigrateMain` | `./gradlew migrate` |
| `com.lemfi.send.seed` | parses `seed/*.json` and calls `SeedInserter.insert`, which you finish once your schema exists |
| `src/main/resources/db/migration` | one empty migration; the schema is yours |

There is no client for the credit service. You write it.
