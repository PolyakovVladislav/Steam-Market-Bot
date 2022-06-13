package com.example.steammarketbot.core.profileInfo.lot;

import java.util.Objects;

public class Lot implements Comparable<Lot> {

    private final int appid;
    private final long id;
    private final String urlName;
    private final String itemName;

    public Lot(int appid,
               long id,
               String urlName,
               String itemName) {
        this.appid = appid;
        this.id = id;
        this.urlName = urlName;
        this.itemName = itemName;
    }

    public long getId() {
        return id;
    }

    public String getUrlName() {
        return urlName;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public int compareTo(Lot o) {
        if (o.id == id)
            return 0;
        else
            return 1;
    }

    public int getAppid() {
        return appid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lot)) return false;
        Lot lot = (Lot) o;
        return appid == lot.appid && id == lot.id && Objects.equals(urlName, lot.urlName) && Objects.equals(itemName, lot.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appid, id, urlName, itemName);
    }
}
