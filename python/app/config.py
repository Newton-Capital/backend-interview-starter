"""Settings from the environment, the same names as the other scaffolds."""

import os
from dataclasses import dataclass
from pathlib import Path


def _first_dir(*candidates: str) -> str:
    for c in candidates:
        if Path(c).is_dir():
            return c
    return candidates[0]


@dataclass(frozen=True)
class Config:
    port: int
    database_url: str
    stub_url: str
    seed_dir: str


def load() -> Config:
    return Config(
        port=int(os.environ.get("PORT", "8080")),
        database_url=os.environ.get("DATABASE_URL", "postgres://interview:interview@localhost:5432/send?sslmode=disable"),
        stub_url=os.environ.get("STUB_URL", "http://localhost:9000"),
        seed_dir=os.environ.get("SEED_DIR") or _first_dir("../seed", "../../seed"),
    )
