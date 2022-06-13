package com.example.steammarketbot.core.items;

public class ItemPageListing {

    //function BuyMarketListing( sElementPrefix, listingid, appid, contextid, itemid )
    private static final String prefix = "listing";
    private final long listingID;
    private final int appId;
    private final int contextId;
    private final long itemId;
    private final float price;

    public ItemPageListing(long listingID, int appId, int contextId, long itemId, float price) {
        this.listingID = listingID;
        this.appId = appId;
        this.contextId = contextId;
        this.itemId = itemId;
        this.price = price;
    }

    public static String getPrefix() {
        return prefix;
    }

    public long getListingID() {
        return listingID;
    }

    public int getAppId() {
        return appId;
    }

    public int getContextId() {
        return contextId;
    }

    public long getItemId() {
        return itemId;
    }

    public float getPrice() {
        return price;
    }
}
