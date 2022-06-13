package com.example.steammarketbot.core.httpsClient;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class Cookies implements CookieJar {

    private static final String SHARED_PREFERENCE_NAME = "Cookies";
    private static final String COOKIE_KEY = "Cookies";

    private List<Cookie> cookies;

    Cookies() {
        cookies = new ArrayList<>();
    }

    @NonNull
    @Override
    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
        List<Cookie> mCookies = new ArrayList<>();
        for (Cookie cookie: cookies) {
            if (cookie.matches(url)) {
                if (cookie.persistent()) {
                    if (cookie.expiresAt() <= System.currentTimeMillis())
                        cookies.remove(cookie);
                    else
                        mCookies.add(cookie);
                }
                else
                    mCookies.add(cookie);
            }
        }
        return mCookies;
    }

    @Override
    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> unmodifiableCookieList) {
        boolean found;
        for (Cookie newCookie: unmodifiableCookieList) {
            found = false;
            for (Cookie oldCookie: cookies) {
                if (newCookie.name().equals(oldCookie.name())) {
                    found = true;
                    cookies.remove(oldCookie);
                    cookies.add(newCookie);
                }
                break;
            }
            if (!found)
                cookies.add(newCookie);
        }
    }

    public void addCookie(String domain, String path, String name, String value,long expiresAt) {
        cookies.add(new Cookie.Builder()
                .domain(domain)
                .path(path)
                .name(name)
                .value(value)
                .expiresAt(expiresAt).build());
    }

    public void saveCookies(Context context) {
        deleteNotPersistentCookie();
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(
                COOKIE_KEY,
                new Gson().toJson(cookies))
        .apply();
    }

    public void loadCookies(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        cookies = new Gson().fromJson(
                sharedPreferences.getString(COOKIE_KEY, ""),
                new TypeToken<ArrayList<Cookie>>(){}.getType());
    }

    private void deleteNotPersistentCookie() {
        cookies.removeIf(cookie -> !cookie.persistent());
    }
}