# Go scaffold. Wiring only: nothing in here knows what a payout is.
export GOWORK = off
DATABASE_URL ?= postgres://interview:interview@localhost:5432/send?sslmode=disable
export DATABASE_URL

.PHONY: build run migrate seed smoke test

build: ## compile everything
	go build ./...

run: ## start the service on PORT (default 8080)
	go run ./cmd/service

migrate: ## apply migrations/ to DATABASE_URL
	go run ./cmd/migrate

seed: ## load seed/*.json into your tables (finish internal/seed first)
	go run ./cmd/seed

smoke: build migrate ## pre-session check: it compiles and reaches Postgres

test:
	go test ./...
