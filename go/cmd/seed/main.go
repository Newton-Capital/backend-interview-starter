// Command seed loads seed/*.json into the database through internal/seed.Insert.
package main

import (
	"context"
	"errors"
	"fmt"
	"log"
	"os"

	"sendservice/internal/config"
	"sendservice/internal/db"
	"sendservice/internal/seed"
)

func main() {
	cfg := config.Load()
	data, err := seed.Load(cfg.SeedDir)
	if err != nil {
		log.Fatalf("seed: %v", err)
	}
	fmt.Printf("seed: %d wallets, %d quotes, fee %s%% parsed from %s\n", len(data.Wallets), len(data.Quotes), data.Fees.Wallet.FeePercent, cfg.SeedDir)

	ctx := context.Background()
	pool, err := db.Connect(ctx, cfg.DatabaseURL)
	if err != nil {
		log.Fatal(err)
	}
	defer pool.Close()

	if err := seed.Insert(ctx, pool, data); err != nil {
		if errors.Is(err, seed.ErrNotImplemented) {
			fmt.Fprintln(os.Stderr, "seed:", err)
			os.Exit(2)
		}
		log.Fatalf("seed: %v", err)
	}
	fmt.Println("seed: loaded")
}
