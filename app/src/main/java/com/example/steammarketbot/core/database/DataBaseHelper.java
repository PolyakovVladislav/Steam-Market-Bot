package com.example.steammarketbot.core.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    public final String DATABASE_NAME;
    public static final int DATABASE_VERSION = 1;
    private final Contract contract;

    public DataBaseHelper(@Nullable Context context, Contract contract, String dataBaseName) {
        super(context, dataBaseName, null, DATABASE_VERSION);
        this.contract = contract;
        DATABASE_NAME = dataBaseName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(contract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    //Сохранение в базу данных (архив)

    /*public void saveItems(Context context) {
        ContentValues contentValues;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, new ItemsContract(), SteamMarketBot.DATABASE_NAME);
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        Item item;
        for(int i = 0; i < itemsCount(); i++) {
            item = getItem(i);
            contentValues = new ContentValues();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap = item.getImage();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_IMAGE, bos.toByteArray());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_ITEM_NAME, item.getItemName());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_COUNT, item.getCount());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_BUDGET, item.getBudget());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_BUY_ON, item.isBuyOn());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_YOU_RECEIVE, item.getYouReceive());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_BUYER_PAY, item.getBuyerPay());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_SELL_ON, item.isSellOn());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_ORDER_BUY, item.isOrderBuyAvailable());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_MANUAL_BUY, item.isManualBuyAvailable());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_CATEGORY, item.getCategory());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_APPID, item.getAppid());
            contentValues.put(ItemsContract.Entry.COLUMN_NAME_URL_NAME, item.getUrlName());
            database.insert(ItemsContract.Entry.TABLE_NAME, null, contentValues);
        }
        dataBaseHelper = new DataBaseHelper(context, new CategoriesContract(), SteamMarketBot.DATABASE_NAME);
        database = dataBaseHelper.getWritableDatabase();
        Category category;
        for(int i = 0; i < categoriesCount(); i++) {
            category = categories.get(i);
            contentValues = new ContentValues();
            contentValues.put(CategoriesContract.Entry.COLUMN_NAME_CATEGORY_NAME, category.getName());
            contentValues.put(CategoriesContract.Entry.COLUMN_NAME_BUY_ON, category.isBuyOn());
            contentValues.put(CategoriesContract.Entry.COLUMN_NAME_SELL_ON, category.isSellOn());
            contentValues.put(CategoriesContract.Entry.COLUMN_NAME_ORDER_COUNT, category.getOrderCount());
            contentValues.put(CategoriesContract.Entry.COLUMN_NAME_CHECK_ORDERS_MIN, category.getCheckOrdersMin());
            contentValues.put(CategoriesContract.Entry.COLUMN_NAME_ORDERS_ONLY, category.isOrdersOnly());
            database.insert(CategoriesContract.Entry.TABLE_NAME, null, contentValues);
        }
    }

    public void loadItems(Context context) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, new ItemsContract(), SteamMarketBot.DATABASE_NAME);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
        String[] projection = {
                ItemsContract.Entry.COLUMN_NAME_IMAGE,
                ItemsContract.Entry.COLUMN_NAME_ITEM_NAME,
                ItemsContract.Entry.COLUMN_NAME_COUNT,
                ItemsContract.Entry.COLUMN_NAME_BUDGET,
                ItemsContract.Entry.COLUMN_NAME_BUY_ON,
                ItemsContract.Entry.COLUMN_NAME_YOU_RECEIVE,
                ItemsContract.Entry.COLUMN_NAME_BUYER_PAY,
                ItemsContract.Entry.COLUMN_NAME_SELL_ON,
                ItemsContract.Entry.COLUMN_NAME_ORDER_BUY,
                ItemsContract.Entry.COLUMN_NAME_MANUAL_BUY,
                ItemsContract.Entry.COLUMN_NAME_CATEGORY,
                ItemsContract.Entry.COLUMN_NAME_APPID,
                ItemsContract.Entry.COLUMN_NAME_URL_NAME
        };
        String selectQuery = "SELECT * FROM " + ItemsContract.Entry.TABLE_NAME;
        Cursor cursor = database.query(ItemsContract.Entry.TABLE_NAME, projection, selectQuery,
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Bitmap bitmap;
            String itemName;
            int count;
            float budget;
            boolean buyOn;
            float youReceive;
            float buyerPay;
            boolean sellOn;
            boolean orderBuy;
            boolean manualBuy;
            String category;
            String appid;
            String urlName;
            do {
                bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_IMAGE)), 0, cursor.getBlob(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_IMAGE)).length);
                itemName = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_ITEM_NAME));
                count = cursor.getInt(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_COUNT));
                budget = cursor.getFloat(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_BUDGET));
                buyOn = cursor.getInt(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_BUY_ON)) == 1;
                youReceive = cursor.getFloat(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_YOU_RECEIVE));
                buyerPay = cursor.getFloat(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_BUYER_PAY));
                sellOn = cursor.getInt(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_SELL_ON)) == 1;
                orderBuy = cursor.getInt(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_ORDER_BUY)) == 1;
                manualBuy = cursor.getInt(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_MANUAL_BUY)) == 1;
                category = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_CATEGORY));
                appid = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_APPID));
                urlName = cursor.getString(cursor.getColumnIndexOrThrow(ItemsContract.Entry.COLUMN_NAME_URL_NAME));

                items.add(new Item(bitmap, itemName,count, budget, buyOn, youReceive, buyerPay, sellOn, orderBuy, manualBuy, category, appid, urlName));
            } while (cursor.moveToNext());
            cursor.close();
        }
        projection = new String[]{
                CategoriesContract.Entry.COLUMN_NAME_CATEGORY_NAME,
                CategoriesContract.Entry.COLUMN_NAME_BUY_ON,
                CategoriesContract.Entry.COLUMN_NAME_SELL_ON,
                CategoriesContract.Entry.COLUMN_NAME_ORDER_COUNT,
                CategoriesContract.Entry.COLUMN_NAME_CHECK_ORDERS_MIN,
                CategoriesContract.Entry.COLUMN_NAME_ORDERS_ONLY
        };
        selectQuery = "SELECT * FROM " + CategoriesContract.Entry.TABLE_NAME;
        cursor = database.query(CategoriesContract.Entry.TABLE_NAME, projection, selectQuery,
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String name;
            boolean buyOn;
            boolean sellOn;
            int orderCount;
            int checkOrdersMin;
            boolean ordersOnly;
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(CategoriesContract.Entry.COLUMN_NAME_CATEGORY_NAME));
                buyOn = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesContract.Entry.COLUMN_NAME_BUY_ON)) == 1;
                sellOn = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesContract.Entry.COLUMN_NAME_SELL_ON)) == 1;
                orderCount = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesContract.Entry.COLUMN_NAME_ORDER_COUNT));
                checkOrdersMin = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesContract.Entry.COLUMN_NAME_CHECK_ORDERS_MIN));
                ordersOnly = cursor.getInt(cursor.getColumnIndexOrThrow(CategoriesContract.Entry.COLUMN_NAME_ORDERS_ONLY)) == 1;

                categories.add(new Category(name, buyOn, sellOn, orderCount, checkOrdersMin, ordersOnly));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public void saveCookies(Context context) {
        ContentValues contentValues;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, new CookiesContract(), SteamMarketBot.DATABASE_NAME);
        SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
        for(Cookie cookie: cookies) {
            contentValues = new ContentValues();
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_NAME, cookie.name());
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_VALUE, cookie.value());
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_EXPIRES_AT, cookie.expiresAt());
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_DOMAIN, cookie.domain());
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_PATH, cookie.path());
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_SECURE, cookie.secure());
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_HTTP_ONLY, cookie.httpOnly());
            contentValues.put(CookiesContract.Entry.COLUMN_NAME_HOST_ONLY, cookie.hostOnly());
            database.insert(CookiesContract.Entry.TABLE_NAME, null, contentValues);
        }
    }

    public void loadCookies(Context context) {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context, new CookiesContract(), SteamMarketBot.DATABASE_NAME);
        SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
        String[] projection = {
                CookiesContract.Entry.COLUMN_NAME_NAME,
                CookiesContract.Entry.COLUMN_NAME_VALUE,
                CookiesContract.Entry.COLUMN_NAME_EXPIRES_AT,
                CookiesContract.Entry.COLUMN_NAME_DOMAIN,
                CookiesContract.Entry.COLUMN_NAME_PATH,
                CookiesContract.Entry.COLUMN_NAME_SECURE,
                CookiesContract.Entry.COLUMN_NAME_HTTP_ONLY,
                CookiesContract.Entry.COLUMN_NAME_HOST_ONLY
        };
        String selectQuery = "SELECT * FROM " + ItemsContract.Entry.TABLE_NAME;
        Cursor cursor = database.query(CookiesContract.Entry.TABLE_NAME, projection, selectQuery,
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            String name;
            String value;
            long expireAt;
            String domain;
            String path;
            boolean secure;
            boolean httpOnly;
            boolean hostOnly;
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_NAME));
                value = cursor.getString(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_VALUE));
                expireAt = cursor.getLong(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_EXPIRES_AT));
                domain = cursor.getString(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_DOMAIN));
                path = cursor.getString(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_PATH));
                secure = cursor.getInt(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_SECURE)) == 1;
                httpOnly = cursor.getInt(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_HTTP_ONLY)) == 1;
                hostOnly = cursor.getInt(cursor.getColumnIndexOrThrow(CookiesContract.Entry.COLUMN_NAME_HOST_ONLY)) == 1;
                Cookie.Builder builder = new Cookie.Builder()
                        .name(name)
                        .value(value)
                        .expiresAt(expireAt)
                        .domain(domain)
                        .path(path);
                if (secure)
                    builder.secure();
                if (httpOnly)
                    builder.httpOnly();
                if (hostOnly)
                    builder.httpOnly();
                cookies.add(builder.build());

            } while (cursor.moveToNext());
            cursor.close();
        }

    }*/
}
