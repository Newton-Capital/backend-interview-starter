"""`python -m seed`: parse seed/*.json and load it through seed.insert."""

import sys

from app import config, db
from seed.insert import NotImplemented_, insert
from seed.load import load

settings = config.load()
data = load(settings.seed_dir)
print(f"seed: {len(data.wallets)} wallets, {len(data.quotes)} quotes, fee {data.fees.fee_percent}% parsed from {settings.seed_dir}")

conn = db.connect(settings.database_url)
try:
    insert(conn, data)
    conn.commit()
    print("seed: loaded")
except NotImplemented_ as e:
    print(f"seed: {e}", file=sys.stderr)
    sys.exit(2)
finally:
    conn.close()
