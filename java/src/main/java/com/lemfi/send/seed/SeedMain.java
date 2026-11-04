package com.lemfi.send.seed;

import com.lemfi.send.AppConfig;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

/** `./gradlew seed`: parse seed/*.json and load it through SeedInserter. */
public final class SeedMain {
    public static void main(String[] args) throws Exception {
        Path dir = AppConfig.seedDir();
        SeedData data = SeedLoader.load(dir);
        System.out.printf("seed: %d wallets, %d quotes, fee %s%% parsed from %s%n",
                data.wallets().size(), data.quotes().size(), data.fees().wallet().feePercent(), dir);

        AppConfig.Jdbc j = AppConfig.Jdbc.from(AppConfig.databaseUrl());
        DataSource ds = DataSourceBuilder.create().url(j.url()).username(j.user()).password(j.password()).build();
        try (var c = ds.getConnection()) {
            c.createStatement().execute("select 1");
        }
        try {
            SeedInserter.insert(ds, data);
            System.out.println("seed: loaded");
        } catch (SeedInserter.NotImplemented e) {
            System.err.println("seed: " + e.getMessage());
            System.exit(2);
        }
    }
}
