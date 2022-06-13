package com.example.steammarketbot.core.profileInfo.lot;

import com.example.steammarketbot.core.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LotForSell extends Lot {

    private final Date placedDate;
    private final float buyerPays;
    private final float youReceive;
    private final long itemId;
    private final int contextId;

    public LotForSell(long listingId, int appid, String urlName, String itemName,
                      Date placedDate, float buyerPays, float youReceive,
                      long itemId, int contextId) {
        super(appid, listingId, urlName, itemName);
        this.placedDate = placedDate;
        this.buyerPays = buyerPays;
        this.youReceive = youReceive;
        this.itemId = itemId;
        this.contextId = contextId;
    }

    public Date getPlacedDate() {
        return placedDate;
    }

    public float getBuyerPays() {
        return buyerPays;
    }

    public float getYouReceive() {
        return youReceive;
    }

    public long getItemId() {
        return itemId;
    }

    public int getContextId() {
        return contextId;
    }

    public static Date parseDate(String date) {
        try {
        date = date.trim();
        SimpleDateFormat simpleDateFormat;
        if (!Strings.find(date, "[0-9]{1,2}\\s.+\\s[0-9]{2,4}")) {
            simpleDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
            date = date + " " + simpleDateFormat.format(System.currentTimeMillis());
        }
        if (Locale.getDefault().equals(new Locale("ru", "RU"))) {
            String month = Strings.search(date, "\\s", "\\s");
            if (month.contains("мая"))
                month = "май";
            date = date.replace(month, month + ".");

        }
        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0);
        }
    }
}
