package com.example.steammarketbot.core.alarmManager;

import android.content.Context;
import android.os.PowerManager;

public class BotWakeLock {

    private static final String STEAM_MARKET_BOT_WAKELOCK_TAG = "steam_market_bot:wakelock_tag";
    private final PowerManager.WakeLock wakeLock;
    private long timeout;

    public BotWakeLock(Context context, long timeout) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, STEAM_MARKET_BOT_WAKELOCK_TAG);
        this.timeout = timeout;
    }

    public void acquire() {
        wakeLock.acquire(timeout);
    }

    public void release() {
        wakeLock.release();
    }

    public void setTimeout (long timeout) {
        if (timeout <= 10 * 60 * 1000)
            this.timeout = timeout;
    }
}
