package com.lemfi.send.seed;

import javax.sql.DataSource;

/**
 * Writes the seed into your tables. It must be safe to run more than once:
 * the harness expects the stated balances at the start of each run, so an
 * upsert that resets balances is the usual shape.
 */
public final class SeedInserter {
    public static final class NotImplemented extends RuntimeException {
        NotImplemented() {
            super("seed insert not implemented: map SeedData onto your tables in SeedInserter.insert");
        }
    }

    // TODO(candidate): replace the throw below with your insert.
    public static void insert(DataSource dataSource, SeedData data) {
        throw new NotImplemented();
    }
}
