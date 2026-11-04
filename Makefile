# Every command for the kit. The first group is what a candidate runs before
# the session; the second runs in the session once the harness tarball is
# loaded; the third is for maintainers and never ships. tools/build-starter.sh
# copies the first two groups into the starter's Makefile, so keep the
# "## Maintainer" marker where it is.
SHELL := /bin/bash

# Local settings live in .env (see .env.example): KEY=VALUE, one per line.
# Docker Compose reads the same file, so a POSTGRES_PORT set there moves the
# host port for `make up` and for `make reference` alike.
-include .env
VERSION ?= 0.1.0-rc2
LEVEL ?= senior
POSTGRES_PORT ?= 5432
# The scaffold the candidate chose: go, java, typescript or python. Set it in .env.
SCAFFOLD ?=
# The scaffolds sit at the root of this repo: go/, java/, typescript/, python/.
SCAFFOLD_DIR ?= .
DATABASE_URL ?= postgres://interview:interview@localhost:$(POSTGRES_PORT)/send?sslmode=disable
STUB_IMAGE ?= ghcr.io/newton-capital/interview-credit-stub:$(VERSION)
HARNESS_IMAGE ?= ghcr.io/newton-capital/interview-harness:$(VERSION)
export STUB_IMAGE HARNESS_IMAGE LEVEL POSTGRES_PORT DATABASE_URL LOG_FORMAT DEFAULT_LIMIT CANDIDATE_URL
COMPOSE_CHECK = docker compose --profile check

.PHONY: help up down smoke seed run reset stub-logs harness-load tier1 tier2 scenario report

help:
	@grep -hE '^[a-z-]+:.*## ' $(MAKEFILE_LIST) | awk -F ':.*## ' '{printf "  %-14s %s\n", $$1, $$2}'

## Candidate, pre-session

up: ## Start the credit stub and Postgres, wait for both health checks
	docker compose up -d --wait

smoke: ## Health of the stub and Postgres, then build the chosen scaffold and run its empty migration
	@curl -fsS http://localhost:9000/healthz >/dev/null && echo "stub: ok" || { echo "stub: not healthy"; exit 1; }
	@docker compose exec -T postgres psql -U interview -d send -tAc 'select 1' >/dev/null && echo "postgres: ok" || { echo "postgres: not healthy"; exit 1; }
	@$(MAKE) --no-print-directory scaffold-target TARGET=smoke
	@echo "$(SCAFFOLD): ok"

seed: ## Load seed/*.json into the candidate's database through the chosen scaffold's seed command
	@$(MAKE) --no-print-directory scaffold-target TARGET=seed

run: ## Start the candidate's service from the chosen scaffold
	@$(MAKE) --no-print-directory scaffold-target TARGET=run

# Delegates to scaffolds/$(SCAFFOLD)/Makefile; refuses clearly when SCAFFOLD is unset.
scaffold-target:
	@test -n "$(SCAFFOLD)" || { echo "set SCAFFOLD=go|java|typescript|python in .env (or make $(TARGET) SCAFFOLD=go)"; exit 2; }
	@test -d $(SCAFFOLD_DIR)/$(SCAFFOLD) || { echo "no scaffold named '$(SCAFFOLD)'; choose go, java, typescript or python"; exit 2; }
	$(MAKE) --no-print-directory -C $(SCAFFOLD_DIR)/$(SCAFFOLD) $(TARGET)

down: ## Stop and remove containers and the volume, so the next `make up` is clean
	docker compose down -v --remove-orphans

reset: down up ## Clean slate: down then up

## In session

stub-logs: ## Tail the stub's request log in a second terminal
	docker compose logs -f credit-snpl

harness-load: ## Download the session tarball the interviewer pastes (URL=...) and load it as interview-harness:session
	@test -n "$(URL)" || { echo "usage: make harness-load URL=<link>"; exit 2; }
	curl -fsSL "$(URL)" -o /tmp/interview-harness.tar
	docker load -i /tmp/interview-harness.tar
	@echo "loaded; run make tier1 LEVEL=$(LEVEL)"

tier1: ## Run Tier 1 for LEVEL (senior or mid), one line per scenario, report in out/tier1.html
	@mkdir -p out
	$(COMPOSE_CHECK) run --rm harness --tier 1 --level $(LEVEL) --report /out/tier1.html

tier2: ## Run the interviewer's scenarios for LEVEL, report in out/tier2.html
	@mkdir -p out
	$(COMPOSE_CHECK) run --rm harness --tier 2 --level $(LEVEL) --report /out/tier2.html

scenario: ## Run one scenario by id, ID=<id> (several: ID=<id>,<id>), report in out/<ID>.html
	@test -n "$(ID)" || { echo "usage: make scenario ID=<id>"; exit 2; }
	@mkdir -p out
	$(COMPOSE_CHECK) run --rm harness --tier all --level $(LEVEL) --only $(ID) --report /out/$(ID).html

report: ## Open the newest report in out/ in the browser (a specific one: REPORT=out/<file>.html)
	@f="$(REPORT)"; [ -n "$$f" ] || f=$$(ls -t out/*.html 2>/dev/null | head -1); \
	[ -n "$$f" ] || { echo "no report in out/; run make tier1 first"; exit 2; }; \
	[ -f "$$f" ] || { echo "$$f: no such file"; exit 2; }; \
	echo "opening $$f"; \
	if command -v open >/dev/null 2>&1; then open "$$f"; \
	elif command -v xdg-open >/dev/null 2>&1; then xdg-open "$$f"; \
	else echo "open $$f in a browser"; fi

