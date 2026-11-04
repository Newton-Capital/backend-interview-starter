"""Alembic environment: the URL comes from DATABASE_URL, as everywhere else."""

from logging.config import fileConfig

from alembic import context
from sqlalchemy import create_engine

from app import config as app_config

alembic_config = context.config
if alembic_config.config_file_name is not None:
    fileConfig(alembic_config.config_file_name)

# SQLAlchemy wants postgresql+psycopg://; the rest of the kit uses postgres://.
url = app_config.load().database_url
for prefix in ("postgres://", "postgresql://"):
    if url.startswith(prefix):
        url = "postgresql+psycopg://" + url[len(prefix):]
        break

target_metadata = None


def run_migrations_online() -> None:
    engine = create_engine(url)
    with engine.connect() as connection:
        context.configure(connection=connection, target_metadata=target_metadata)
        with context.begin_transaction():
            context.run_migrations()


run_migrations_online()
