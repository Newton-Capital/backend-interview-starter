"""init

Your schema goes here. The scaffold creates no tables: the data model is yours.
Add the next migration with `.venv/bin/alembic revision -m "<name>"`.
"""

from alembic import op

revision = "0001"
down_revision = None
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.execute("select 1")


def downgrade() -> None:
    op.execute("select 1")
