package com.example.steammarketbot.core.profileInfo;

import java.util.Objects;

public class Wallet {

    private final float walletBalance;
    private final float walletCurrency;
    private final String walletCountry;
    private final float walletFee;
    private final float walletFeeMinimum;
    private final float walletFeePercent;
    private final float walletPublisherFeePercent;
    private final float walletFeeBase;
    private final long timestamp;

    public Wallet(float walletBalance,
                  float walletCurrency,
                  String walletCountry,
                  float walletFee,
                  float walletFeeMinimum,
                  float walletFeePercent,
                  float walletPublisherFeePercent,
                  float walletFeeBase,
                  long timestamp) {
        this.walletBalance = walletBalance;
        this.walletCurrency = walletCurrency;
        this.walletCountry = walletCountry;
        this.walletFee = walletFee;
        this.walletFeeMinimum = walletFeeMinimum;
        this.walletFeePercent = walletFeePercent;
        this.walletPublisherFeePercent = walletPublisherFeePercent;
        this.walletFeeBase = walletFeeBase;
        this.timestamp = timestamp;
    }

    public float getWalletBalance() {
        return walletBalance;
    }

    public float getWalletCurrency() {
        return walletCurrency;
    }

    public String getWalletCountry() {
        return walletCountry;
    }

    public float getWalletFee() {
        return walletFee;
    }

    public float getWalletFeeMinimum() {
        return walletFeeMinimum;
    }

    public float getWalletFeePercent() {
        return walletFeePercent;
    }

    public float getWalletPublisherFeePercent() {
        return walletPublisherFeePercent;
    }

    public float getWalletFeeBase() {
        return walletFeeBase;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return Float.compare(wallet.walletBalance, walletBalance) == 0 && Float.compare(wallet.walletCurrency, walletCurrency) == 0 && Float.compare(wallet.walletFee, walletFee) == 0 && Float.compare(wallet.walletFeeMinimum, walletFeeMinimum) == 0 && Float.compare(wallet.walletFeePercent, walletFeePercent) == 0 && Float.compare(wallet.walletPublisherFeePercent, walletPublisherFeePercent) == 0 && Float.compare(wallet.walletFeeBase, walletFeeBase) == 0 && timestamp == wallet.timestamp && walletCountry.equals(wallet.walletCountry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletBalance, walletCurrency, walletCountry, walletFee, walletFeeMinimum, walletFeePercent, walletPublisherFeePercent, walletFeeBase, timestamp);
    }
}
