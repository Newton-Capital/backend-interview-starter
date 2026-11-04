# TypeScript scaffold. Wiring only: nothing in here knows what a payout is.
DATABASE_URL ?= postgres://interview:interview@localhost:5432/send?sslmode=disable
export DATABASE_URL

.PHONY: build run migrate seed smoke test

build: node_modules ## install and type-check
	npm run --silent build

node_modules: package-lock.json
	npm ci --silent
	@touch node_modules

run: node_modules ## start the service on PORT (default 8080), reloading on change
	npm run dev

migrate: node_modules ## apply migrations/ to DATABASE_URL
	npm run --silent migrate

seed: node_modules ## load seed/*.json into your tables (finish src/seed/insert.ts first)
	npm run --silent seed

smoke: build migrate ## pre-session check: it type-checks and reaches Postgres

test: node_modules
	npm test
