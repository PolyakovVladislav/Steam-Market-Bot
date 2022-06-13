package com.example.steammarketbot.core.profileInfo.lot;

import java.util.Objects;

public class LotForBuy extends Lot {

    private final int count;
    private final float budget;

    public LotForBuy(int appid, long listingId, String urlName, String itemName, int count, float budget) {
        super(appid, listingId, urlName, itemName);
        this.count = count;
        this.budget = budget;
    }

    public int getCount() {
        return count;
    }

    public float getBudget() {
        return budget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LotForBuy)) return false;
        if (!super.equals(o)) return false;
        LotForBuy lotForBuy = (LotForBuy) o;
        return count == lotForBuy.count && Float.compare(lotForBuy.budget, budget) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), count, budget);
    }
}
