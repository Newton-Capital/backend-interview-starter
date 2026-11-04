// Command migrate applies migrations/*.sql to DATABASE_URL with golang-migrate.
// Run it from the scaffold directory (make migrate), or set MIGRATIONS_DIR.
package main

import (
	"errors"
	"log"
	"os"
	"strings"

	"github.com/golang-migrate/migrate/v4"
	_ "github.com/golang-migrate/migrate/v4/database/pgx/v5"
	_ "github.com/golang-migrate/migrate/v4/source/file"

	"sendservice/internal/config"
)

func main() {
	cfg := config.Load()
	dir := os.Getenv("MIGRATIONS_DIR")
	if dir == "" {
		dir = "migrations"
	}
	m, err := migrate.New("file://"+dir, pgxURL(cfg.DatabaseURL))
	if err != nil {
		log.Fatal(err)
	}
	defer m.Close()
	switch err := m.Up(); {
	case errors.Is(err, migrate.ErrNoChange):
		log.Println("migrations: nothing to apply")
	case err != nil:
		log.Fatal(err)
	default:
		log.Println("migrations: applied")
	}
}

// golang-migrate's pgx driver wants a pgx5:// scheme.
func pgxURL(url string) string {
	for _, prefix := range []string{"postgres://", "postgresql://"} {
		if strings.HasPrefix(url, prefix) {
			return "pgx5://" + strings.TrimPrefix(url, prefix)
		}
	}
	return url
}
