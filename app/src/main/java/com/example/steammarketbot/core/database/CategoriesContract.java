package com.example.steammarketbot.core.database;

import android.provider.BaseColumns;

public class CategoriesContract extends Contract {

    public CategoriesContract() {
        super("CREATE TABLE " + Entry.TABLE_NAME + " (" +
                Entry._ID + " INTEGER PRIMARY KEY," +
                Entry.COLUMN_NAME_CATEGORY_NAME + " TEXT," +
                Entry.COLUMN_NAME_BUY_ON + "BIT," +
                Entry.COLUMN_NAME_SELL_ON + " BIT," +
                Entry.COLUMN_NAME_ORDER_COUNT + " INT," +
                Entry.COLUMN_NAME_CHECK_ORDERS_MIN + " INT," +
                Entry.COLUMN_NAME_ORDERS_ONLY + " BIT)");
    }

    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String COLUMN_NAME_CATEGORY_NAME = "category_name";
        public static final String COLUMN_NAME_BUY_ON = "buy_on";
        public static final String COLUMN_NAME_SELL_ON = "sell_on";
        public static final String COLUMN_NAME_ORDER_COUNT = "order_count";
        public static final String COLUMN_NAME_CHECK_ORDERS_MIN = "check_orders_min";
        public static final String COLUMN_NAME_ORDERS_ONLY = "orders_only";

    }
}
