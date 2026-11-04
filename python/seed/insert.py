"""Writes the seed into your tables.

It must be safe to run more than once: the harness expects the stated
balances at the start of each run, so an upsert that resets balances is the
usual shape.
"""

import psycopg

from seed.load import SeedData


class NotImplemented_(Exception):
    def __init__(self) -> None:
        super().__init__("seed insert not implemented: map SeedData onto your tables in seed/insert.py (insert)")


def insert(conn: psycopg.Connection, data: SeedData) -> None:
    # TODO(candidate): replace the raise below with your insert.
    raise NotImplemented_()
