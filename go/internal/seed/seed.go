// Package seed parses and validates seed/wallets.json and seed/fees.json.
// Loading and validation are done; Insert is yours, because only you know
// your tables. The harness assumes these wallets exist with these balances
// at the start of every run.
package seed

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"os"
	"path/filepath"
	"regexp"

	"github.com/jackc/pgx/v5/pgxpool"
)

// Data is the content of both seed files.
type Data struct {
	Wallets []Wallet `json:"wallets"`
	Quotes  []Quote  `json:"quotes"`
	Fees    Fees     `json:"-"`
}

type Wallet struct {
	WalletID string `json:"walletId"`
	UserID   string `json:"userId"`
	Currency string `json:"currency"`
	Balance  string `json:"balance"` // decimal string, two places
}

// Quote is a pre-made quote; the seed carries one that has already expired.
type Quote struct {
	QuoteID       string `json:"quoteId"`
	UserID        string `json:"userId"`
	WalletID      string `json:"walletId"`
	FundingSource string `json:"fundingSource"`
	Amount        string `json:"amount"`
	Fee           string `json:"fee"`
	Total         string `json:"total"`
	ExpiresAt     string `json:"expiresAt"` // RFC 3339
}

type Fees struct {
	QuoteTTLSeconds int `json:"quoteTtlSeconds"`
	Wallet          struct {
		FeePercent string            `json:"feePercent"`
		MinimumFee map[string]string `json:"minimumFee"`
		Rounding   string            `json:"rounding"`
	} `json:"wallet"`
}

var twoPlaces = regexp.MustCompile(`^[0-9]+\.[0-9]{2}$`)

// Load reads and validates both files from dir.
func Load(dir string) (*Data, error) {
	var d Data
	if err := readJSON(filepath.Join(dir, "wallets.json"), &d); err != nil {
		return nil, err
	}
	if err := readJSON(filepath.Join(dir, "fees.json"), &d.Fees); err != nil {
		return nil, err
	}
	if err := d.Validate(); err != nil {
		return nil, err
	}
	return &d, nil
}

func readJSON(path string, v any) error {
	b, err := os.ReadFile(path)
	if err != nil {
		return err
	}
	if err := json.Unmarshal(b, v); err != nil {
		return fmt.Errorf("%s: %w", path, err)
	}
	return nil
}

// Validate checks the shape the rest of the kit relies on.
func (d *Data) Validate() error {
	if len(d.Wallets) == 0 {
		return errors.New("wallets.json: no wallets")
	}
	seen := map[string]bool{}
	for _, w := range d.Wallets {
		switch {
		case w.WalletID == "" || w.UserID == "" || w.Currency == "":
			return fmt.Errorf("wallet %+v: walletId, userId and currency are required", w)
		case !twoPlaces.MatchString(w.Balance):
			return fmt.Errorf("wallet %s: balance %q is not a two-place decimal string", w.WalletID, w.Balance)
		case seen[w.WalletID]:
			return fmt.Errorf("wallet %s appears twice", w.WalletID)
		}
		seen[w.WalletID] = true
		if _, ok := d.Fees.Wallet.MinimumFee[w.Currency]; !ok {
			return fmt.Errorf("fees.json has no minimum fee for %s (wallet %s)", w.Currency, w.WalletID)
		}
	}
	for _, q := range d.Quotes {
		if q.QuoteID == "" || !seen[q.WalletID] || !twoPlaces.MatchString(q.Amount) || !twoPlaces.MatchString(q.Fee) || !twoPlaces.MatchString(q.Total) || q.ExpiresAt == "" {
			return fmt.Errorf("quote %+v is incomplete or refers to an unknown wallet", q)
		}
	}
	if d.Fees.QuoteTTLSeconds <= 0 || d.Fees.Wallet.FeePercent == "" {
		return errors.New("fees.json: quoteTtlSeconds and wallet.feePercent are required")
	}
	return nil
}

// ErrNotImplemented is returned by Insert until you write it.
var ErrNotImplemented = errors.New("seed insert not implemented: map Data onto your tables in internal/seed/seed.go (Insert)")

// Insert writes the seed into your tables. It must be safe to run more than
// once: the harness expects the stated balances at the start of each run,
// so an upsert that resets balances is the usual shape.
//
// TODO(candidate): replace the return below with your insert.
func Insert(ctx context.Context, pool *pgxpool.Pool, d *Data) error {
	_ = ctx
	_ = pool
	_ = d
	return ErrNotImplemented
}
