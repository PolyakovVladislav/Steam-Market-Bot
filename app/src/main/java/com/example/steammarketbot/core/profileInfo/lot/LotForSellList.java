package com.example.steammarketbot.core.profileInfo.lot;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

public class LotForSellList {

    private final HashSet<LotForSell> lots;
    private final long timeStamp;

    public LotForSellList(HashSet<LotForSell> lots, long timeStamp) {
        this.lots = lots;
        this.timeStamp = timeStamp;
    }

    public void addSet(HashSet<LotForSell> lots) {
        this.lots.addAll(lots);
    }

    public Iterator<LotForSell> getIterator() {
        return lots.iterator();
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public HashSet<LotForSell> getList() {
        return lots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof LotForSellList))
            return false;
        LotForSellList lotForSellList = (LotForSellList) o;
        return timeStamp == lotForSellList.timeStamp && Objects.equals(lots, lotForSellList.lots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lots, timeStamp);
    }
}
