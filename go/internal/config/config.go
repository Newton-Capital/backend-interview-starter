// Package config reads the service's settings from the environment.
package config

import "os"

// Config is everything the service needs from outside.
type Config struct {
	Port        string // PORT, default 8080
	DatabaseURL string // DATABASE_URL
	StubURL     string // STUB_URL, the Credit SNPL service
	SeedDir     string // SEED_DIR; defaults to ../seed, then ../../seed, whichever exists
}

// Load reads the environment, applying the compose defaults.
func Load() Config {
	return Config{
		Port:        env("PORT", "8080"),
		DatabaseURL: env("DATABASE_URL", "postgres://interview:interview@localhost:5432/send?sslmode=disable"),
		StubURL:     env("STUB_URL", "http://localhost:9000"),
		SeedDir:     env("SEED_DIR", firstDir("../seed", "../../seed")),
	}
}

func env(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}

func firstDir(candidates ...string) string {
	for _, c := range candidates {
		if st, err := os.Stat(c); err == nil && st.IsDir() {
			return c
		}
	}
	return candidates[0]
}
