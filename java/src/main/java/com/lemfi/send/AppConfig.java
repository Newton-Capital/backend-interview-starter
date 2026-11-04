package com.lemfi.send;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Settings from the environment, the same names as the other scaffolds. */
@Configuration
public class AppConfig {
    public static final String DEFAULT_DATABASE_URL = "postgres://interview:interview@localhost:5432/send?sslmode=disable";

    public static String env(String key, String fallback) {
        String v = System.getenv(key);
        return v == null || v.isEmpty() ? fallback : v;
    }

    public static String databaseUrl() { return env("DATABASE_URL", DEFAULT_DATABASE_URL); }

    public static String stubUrl() { return env("STUB_URL", "http://localhost:9000"); }

    /** SEED_DIR, else ../seed, else ../../seed: the layout differs between the starter and the kit. */
    public static Path seedDir() {
        String set = System.getenv("SEED_DIR");
        if (set != null && !set.isEmpty()) return Path.of(set);
        for (String c : new String[] {"../seed", "../../seed"}) {
            if (Files.isDirectory(Path.of(c))) return Path.of(c);
        }
        return Path.of("../seed");
    }

    /** A postgres://user:pass@host:port/db?params URL as the JDBC triple. */
    public record Jdbc(String url, String user, String password) {
        public static Jdbc from(String databaseUrl) {
            URI u = URI.create(databaseUrl);
            String[] userInfo = u.getUserInfo() == null ? new String[] {"", ""} : u.getUserInfo().split(":", 2);
            String query = u.getQuery() == null ? "" : "?" + u.getQuery();
            int port = u.getPort() == -1 ? 5432 : u.getPort();
            return new Jdbc("jdbc:postgresql://" + u.getHost() + ":" + port + u.getPath() + query,
                    userInfo[0], userInfo.length > 1 ? userInfo[1] : "");
        }
    }

    @Bean
    public DataSource dataSource() {
        Jdbc j = Jdbc.from(databaseUrl());
        return DataSourceBuilder.create().url(j.url()).username(j.user()).password(j.password()).build();
    }
}
