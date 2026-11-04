import type pg from "pg";
import type { SeedData } from "./load.js";

export class NotImplemented extends Error {
  constructor() {
    super("seed insert not implemented: map SeedData onto your tables in src/seed/insert.ts (insert)");
  }
}

/**
 * Writes the seed into your tables. It must be safe to run more than once:
 * the harness expects the stated balances at the start of each run, so an
 * upsert that resets balances is the usual shape.
 *
 * TODO(candidate): replace the throw below with your insert.
 */
export async function insert(_pool: pg.Pool, _data: SeedData): Promise<void> {
  throw new NotImplemented();
}
