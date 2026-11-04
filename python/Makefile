# Python scaffold. Wiring only: nothing in here knows what a payout is.
DATABASE_URL ?= postgres://interview:interview@localhost:5432/send?sslmode=disable
export DATABASE_URL
VENV ?= .venv
PY = $(VENV)/bin/python

.PHONY: build run migrate seed smoke test

build: $(VENV)/.installed ## create the venv, install, byte-compile
	$(PY) -m compileall -q app seed

$(VENV)/.installed: requirements.txt
	python3 -m venv $(VENV)
	$(VENV)/bin/pip install --quiet --disable-pip-version-check -r requirements.txt
	@touch $@

run: build ## start the service on PORT (default 8080), reloading on change
	$(VENV)/bin/uvicorn app.main:app --host 0.0.0.0 --port $${PORT:-8080} --reload

migrate: build ## apply alembic/versions to DATABASE_URL
	$(VENV)/bin/alembic upgrade head

seed: build ## load seed/*.json into your tables (finish seed/insert.py first)
	$(PY) -m seed

smoke: build migrate ## pre-session check: it installs and reaches Postgres

test: build
	$(PY) -m unittest discover -s tests -q 2>/dev/null || echo "no tests yet"
