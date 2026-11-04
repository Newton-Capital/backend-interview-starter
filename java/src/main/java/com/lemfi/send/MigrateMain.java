package com.lemfi.send;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

/** `./gradlew migrate`: apply db/migration to DATABASE_URL without starting the app. */
public final class MigrateMain {
    public static void main(String[] args) {
        AppConfig.Jdbc j = AppConfig.Jdbc.from(AppConfig.databaseUrl());
        // The compose database may already hold another tool's tables: baseline at 0 so V1 still runs.
        Flyway flyway = Flyway.configure().dataSource(j.url(), j.user(), j.password())
                .locations("classpath:db/migration").baselineOnMigrate(true).baselineVersion("0").load();
        MigrateResult result = flyway.migrate();
        System.out.println("migrations: " + (result.migrationsExecuted == 0 ? "nothing to apply" : result.migrationsExecuted + " applied"));
    }
}
