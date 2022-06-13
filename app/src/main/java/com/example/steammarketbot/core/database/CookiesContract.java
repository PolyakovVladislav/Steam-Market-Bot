package com.example.steammarketbot.core.database;

import android.provider.BaseColumns;

public class CookiesContract extends Contract {

    public CookiesContract() {
        super("CREATE TABLE " + Entry.TABLE_NAME + " (" +
                CookiesContract.Entry._ID + " INTEGER PRIMARY KEY," +
                CookiesContract.Entry.COLUMN_NAME_NAME + " TEXT," +
                CookiesContract.Entry.COLUMN_NAME_VALUE + "TEXT," +
                CookiesContract.Entry.COLUMN_NAME_EXPIRES_AT + " BIGINT," +
                CookiesContract.Entry.COLUMN_NAME_DOMAIN + " TEXT," +
                CookiesContract.Entry.COLUMN_NAME_PATH + " TEXT," +
                CookiesContract.Entry.COLUMN_NAME_SECURE + " BIT," +
                CookiesContract.Entry.COLUMN_NAME_HTTP_ONLY + " BIT," +
                CookiesContract.Entry.COLUMN_NAME_HOST_ONLY + " BIT)");
    }

    public static class Entry implements BaseColumns {
        public static final String TABLE_NAME = "Cookies";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_EXPIRES_AT = "expires_at";
        public static final String COLUMN_NAME_DOMAIN = "domain";
        public static final String COLUMN_NAME_PATH = "path";
        public static final String COLUMN_NAME_SECURE = "secure";
        public static final String COLUMN_NAME_HTTP_ONLY = "http_only";
        public static final String COLUMN_NAME_HOST_ONLY = "host_only";
    }
}
