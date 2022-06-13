package com.example.steammarketbot.core.profileInfo.lot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

public class LotForBuyList {

    private final HashSet<LotForBuy> lots;
    private final long timeStamp;

    public LotForBuyList(HashSet<LotForBuy> lots, long timeStamp) {
        this.lots = lots;
        this.timeStamp = timeStamp;
    }

    public void addSet(HashSet<LotForBuy> lots) {
        this.lots.addAll(lots);
    }

    public Iterator<LotForBuy> getIterator() {
        return lots.iterator();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public HashSet<LotForBuy> getList() {
        return lots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof LotForBuyList))
            return false;
        LotForBuyList lotForBuyList = (LotForBuyList) o;
        return timeStamp == lotForBuyList.timeStamp && Objects.equals(lots, lotForBuyList.lots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lots, timeStamp);
    }
}
