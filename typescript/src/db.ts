import pg from "pg";

/** A connection pool, pinged once so a bad URL fails at startup. */
export async function connect(databaseUrl: string): Promise<pg.Pool> {
  const pool = new pg.Pool({ connectionString: databaseUrl });
  await pool.query("select 1");
  return pool;
}
