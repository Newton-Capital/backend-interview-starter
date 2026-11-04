package com.lemfi.send.seed;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/** Reads and validates the seed files. Done; the insert is yours. */
public final class SeedLoader {
    private static final Pattern TWO_PLACES = Pattern.compile("^[0-9]+\\.[0-9]{2}$");
    private static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static SeedData load(Path dir) throws IOException {
        JsonNode wallets = MAPPER.readTree(dir.resolve("wallets.json").toFile());
        SeedData.Fees fees = MAPPER.readValue(dir.resolve("fees.json").toFile(), SeedData.Fees.class);
        List<SeedData.Wallet> w = MAPPER.convertValue(wallets.path("wallets"), MAPPER.getTypeFactory().constructCollectionType(List.class, SeedData.Wallet.class));
        List<SeedData.Quote> q = wallets.has("quotes")
                ? MAPPER.convertValue(wallets.path("quotes"), MAPPER.getTypeFactory().constructCollectionType(List.class, SeedData.Quote.class))
                : List.of();
        SeedData data = new SeedData(w, q, fees);
        validate(data);
        return data;
    }

    public static void validate(SeedData d) {
        if (d.wallets() == null || d.wallets().isEmpty()) throw new IllegalArgumentException("wallets.json: no wallets");
        Set<String> seen = new HashSet<>();
        for (SeedData.Wallet w : d.wallets()) {
            if (isBlank(w.walletId()) || isBlank(w.userId()) || isBlank(w.currency()))
                throw new IllegalArgumentException("wallet " + w + ": walletId, userId and currency are required");
            if (w.balance() == null || !TWO_PLACES.matcher(w.balance()).matches())
                throw new IllegalArgumentException("wallet " + w.walletId() + ": balance " + w.balance() + " is not a two-place decimal string");
            if (!seen.add(w.walletId())) throw new IllegalArgumentException("wallet " + w.walletId() + " appears twice");
            if (d.fees().wallet() == null || !d.fees().wallet().minimumFee().containsKey(w.currency()))
                throw new IllegalArgumentException("fees.json has no minimum fee for " + w.currency() + " (wallet " + w.walletId() + ")");
        }
        for (SeedData.Quote q : d.quotes()) {
            if (isBlank(q.quoteId()) || !seen.contains(q.walletId()) || !twoPlaces(q.amount()) || !twoPlaces(q.fee()) || !twoPlaces(q.total()) || isBlank(q.expiresAt()))
                throw new IllegalArgumentException("quote " + q + " is incomplete or refers to an unknown wallet");
        }
        if (d.fees().quoteTtlSeconds() <= 0 || isBlank(d.fees().wallet().feePercent()))
            throw new IllegalArgumentException("fees.json: quoteTtlSeconds and wallet.feePercent are required");
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }

    private static boolean twoPlaces(String s) { return s != null && TWO_PLACES.matcher(s).matches(); }
}
