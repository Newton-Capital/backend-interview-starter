package com.lemfi.send.seed;

import java.util.List;
import java.util.Map;

/** The content of seed/wallets.json and seed/fees.json. */
public record SeedData(List<Wallet> wallets, List<Quote> quotes, Fees fees) {
    public record Wallet(String walletId, String userId, String currency, String balance) {}

    public record Quote(String quoteId, String userId, String walletId, String fundingSource,
                        String amount, String fee, String total, String expiresAt) {}

    public record Fees(int quoteTtlSeconds, WalletFees wallet) {}

    public record WalletFees(String feePercent, Map<String, String> minimumFee, String rounding) {}
}
