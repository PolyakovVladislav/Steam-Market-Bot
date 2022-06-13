package com.example.steammarketbot.core.database;

import android.provider.BaseColumns;

public class ItemsContract extends Contract {

    public ItemsContract() {
        super("CREATE TABLE " + Entry.TABLE_NAME + " (" +
                Entry._ID + " INTEGER PRIMARY KEY," +
                Entry.COLUMN_NAME_IMAGE + " IMAGE," +
                Entry.COLUMN_NAME_ITEM_NAME + "TEXT," +
                Entry.COLUMN_NAME_COUNT + " INT," +
                Entry.COLUMN_NAME_BUDGET + " FLOAT," +
                Entry.COLUMN_NAME_BUY_ON + " BIT," +
                Entry.COLUMN_NAME_YOU_RECEIVE + " FLOAT," +
                Entry.COLUMN_NAME_BUYER_PAY + " FLOAT," +
                Entry.COLUMN_NAME_SELL_ON + " BIT," +
                Entry.COLUMN_NAME_ORDER_BUY + " BIT," +
                Entry.COLUMN_NAME_MANUAL_BUY + " BIT," +
                Entry.COLUMN_NAME_CATEGORY + " TEXT," +
                Entry.COLUMN_NAME_APPID + " TEXT," +
                Entry.COLUMN_NAME_URL_NAME + " TEXT)");
    }

    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_ITEM_NAME = "item_name";
        public static final String COLUMN_NAME_COUNT = "count";
        public static final String COLUMN_NAME_BUDGET = "budget";
        public static final String COLUMN_NAME_BUY_ON = "buy_on";
        public static final String COLUMN_NAME_YOU_RECEIVE = "you_receive";
        public static final String COLUMN_NAME_BUYER_PAY = "buyer_pay";
        public static final String COLUMN_NAME_SELL_ON = "sell_on";
        public static final String COLUMN_NAME_ORDER_BUY = "order_buy";
        public static final String COLUMN_NAME_MANUAL_BUY = "manual_buy";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_APPID = "appid";
        public static final String COLUMN_NAME_URL_NAME = "url_name";
    }
}