# Java scaffold. Wiring only: nothing in here knows what a payout is.
DATABASE_URL ?= postgres://interview:interview@localhost:5432/send?sslmode=disable
export DATABASE_URL

.PHONY: build run migrate seed smoke test

build: ## compile (the first run downloads Gradle and the dependencies)
	./gradlew --quiet --console=plain assemble

run: ## start the service on PORT (default 8080)
	./gradlew --console=plain bootRun

migrate: ## apply src/main/resources/db/migration to DATABASE_URL
	./gradlew --quiet --console=plain migrate

seed: ## load seed/*.json into your tables (finish SeedInserter first)
	./gradlew --quiet --console=plain seed

smoke: build migrate ## pre-session check: it compiles and reaches Postgres

test:
	./gradlew --console=plain test
