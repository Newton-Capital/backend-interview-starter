"""One psycopg connection; pinged so a bad URL fails at startup."""

import psycopg


def connect(database_url: str) -> psycopg.Connection:
    conn = psycopg.connect(database_url, autocommit=False)
    with conn.cursor() as cur:
        cur.execute("select 1")
    return conn
