package com.example.steammarketbot.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.example.steammarketbot.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsManager {

    public static final String SHARED_PREFERENCE_NAME = "Settings";

    public static final String CheckYourListingItemsOnlyOrderSeconds = "CheckYourListingItemsOnlyOrderSeconds";
    public static final String WaitBeforeCheckNextItemMilliseconds = "WaitBeforeCheckNextItemMilliseconds";
    public static final String RandomRateForWaitBeforeCheckNextItemPercent = "RandomRateForWaitBeforeCheckNextItemPercent";
    public static final String WorkSessionLengthMinutes = "WorkSessionLengthMinutes";
    public static final String RandomRateForWorkSessionLengthPercent = "RandomRateForWorkSessionLengthPercent";
    public static final String PauseLengthMinutes = "PauseLengthMinutes";
    public static final String RandomRateForPauseLengthPercent = "RandomRateForPauseLengthPercent";
    public static final String PauseInCaseOfErrorToMuchRequestsLengthMinutes = "PauseInCaseOfErrorToMuchRequestsLengthMinutes";
    public static final String RandomRateForPauseInCaseOfErrorToMuchRequestsLengthPercent = "RandomRateForPauseInCaseOfErrorToMuchRequestsLengthPercent";
    public static final String CheckInventoryOnlyWhilePause = "CheckInventoryOnlyWhilePause";
    public static final String CheckInventoryEveryMinutes = "CheckInventoryEveryMinutes";
    public static final String RandomRateForCheckInventoryEveryPercent = "RandomRateForCheckInventoryEveryPercent";
    public static final String WaitBeforeSellNextItemSeconds = "WaitBeforeSellNextItemSeconds";
    public static final String RandomRateForWaitBeforeSellNextItemPercent = "RandomRateForWaitBeforeSellNextItemPercent";
    public static final String UserAgent = "UserAgent";
    public static final String HttpClientCallTimeoutMilliseconds = "HttpClientCallTimeoutMilliseconds";
    public static final String DefaultInstructionStepDelayMilliseconds = "DefaultInstructionStepDelayMilliseconds";
    public static final String DefaultInstructionRandomRatePercent = "DefaultInstructionRandomRatePercent";
    public static final String UseOnlyWifi = "UseOnlyWifi";
    public static final String WakeLockTimeoutMinutes = "WakeLockTimeoutMinutes";
    public static final String NewAttemptDelaySeconds = "NewAttemptDelaySeconds";
    public static final String MaxHttpRequestCount = "MaxHttpRequestCount";

    SharedPreferences sharedPreferences;
    Map<String, Object> defaults = new HashMap<>();
    OnSettingsChangeListener onSettingsChangeListener;

    SettingsManager (SharedPreferences sharedPreferences, Context context,
                     OnSettingsChangeListener onSettingsChangeListener) {
        this.sharedPreferences = sharedPreferences;
        Resources resources = context.getResources();
        this.onSettingsChangeListener = onSettingsChangeListener;

        defaults.put(CheckYourListingItemsOnlyOrderSeconds, resources.getInteger(R.integer.CheckYourListingItemsOnlyOrderSeconds));
        defaults.put(WaitBeforeCheckNextItemMilliseconds, resources.getDimension(R.dimen.WaitBeforeCheckNextItemMilliseconds));
        defaults.put(RandomRateForWaitBeforeCheckNextItemPercent, resources.getInteger(R.integer.RandomRateForWaitBeforeCheckNextItemPercent));
        defaults.put(WorkSessionLengthMinutes, resources.getInteger(R.integer.WorkSessionLengthMinutes));
        defaults.put(RandomRateForWorkSessionLengthPercent, resources.getInteger(R.integer.RandomRateForWorkSessionLengthPercent));
        defaults.put(PauseLengthMinutes, resources.getInteger(R.integer.PauseLengthMinutes));
        defaults.put(RandomRateForPauseLengthPercent, resources.getInteger(R.integer.RandomRateForPauseLengthPercent));
        defaults.put(PauseInCaseOfErrorToMuchRequestsLengthMinutes, resources.getInteger(R.integer.PauseInCaseOfErrorToMuchRequestsLengthMinutes));
        defaults.put(RandomRateForPauseInCaseOfErrorToMuchRequestsLengthPercent, resources.getInteger(R.integer.RandomRateForPauseInCaseOfErrorToMuchRequestsLengthPercent));
        defaults.put(CheckInventoryOnlyWhilePause, resources.getBoolean(R.bool.CheckInventoryOnlyWhilePause));
        defaults.put(CheckInventoryEveryMinutes, resources.getInteger(R.integer.CheckInventoryEveryMinutes));
        defaults.put(RandomRateForCheckInventoryEveryPercent, resources.getInteger(R.integer.RandomRateForCheckInventoryEveryPercent));
        defaults.put(WaitBeforeSellNextItemSeconds, resources.getInteger(R.integer.WaitBeforeSellNextItemSeconds));
        defaults.put(RandomRateForWaitBeforeSellNextItemPercent, resources.getInteger(R.integer.RandomRateForWaitBeforeSellNextItemPercent));
        defaults.put(UserAgent, resources.getString(R.string.UserAgent));
        defaults.put(HttpClientCallTimeoutMilliseconds, (long) resources.getInteger(R.integer.HttpClientCallTimeoutMilliseconds));
        defaults.put(DefaultInstructionStepDelayMilliseconds, (long) resources.getInteger(R.integer.DefaultInstructionStepDelayMilliseconds));
        defaults.put(DefaultInstructionRandomRatePercent, resources.getInteger(R.integer.DefaultInstructionRandomRatePercent));
        defaults.put(UseOnlyWifi, resources.getBoolean(R.bool.UseOnlyWifi));
        defaults.put(WakeLockTimeoutMinutes, resources.getInteger(R.integer.WakeLockTimeoutMinutes));
        defaults.put(NewAttemptDelaySeconds, resources.getInteger(R.integer.NewAttemptDelaySeconds));
        defaults.put(MaxHttpRequestCount, resources.getInteger(R.integer.MaxHttpRequestCount));
    }

    public void changeSettings(String key, Object value) {
        putValueInSharePreference(key, value);
        onSettingsChangeListener.onSettingChange(key, value);
    }

    public float getFloatSetting(String key) {
        return sharedPreferences.getFloat(key, (float) getDefault(key, Float.class));
    }

    public int getIntSetting(String key) {
        return sharedPreferences.getInt(key, (int) getDefault(key, Integer.class));
    }

    public String getStringSetting(String key) {
            return sharedPreferences.getString(key, (String) getDefault(key, String.class));
    }

    public boolean getBooleanSetting(String key) {
        return sharedPreferences.getBoolean(key, (boolean) getDefault(key, Boolean.class));
    }

    public Long getLongSetting(String key) {
        return sharedPreferences.getLong(key, (long) getDefault(key, Long.class));
    }

    private Object getDefault(String key,Object object) {
        if (defaults.containsKey(key)) {
            if (Objects.equals(defaults.get(key), object))
                return defaults.get(key);
            else
                throw new ClassCastException("Class of value for key " + key + " is not "
                        + object.getClass().getName() + " value");
        }
        else
            throw new RuntimeException("Default settings does not contains key " + key);
    }

    public void loadDefaults() {
        sharedPreferences.edit().clear().apply();
        Object value;
        for (String key: defaults.keySet()) {
            if (defaults.get(key) != null) {
                value = defaults.get(key);
                putValueInSharePreference(key, value);
            }
        }

    }

    private void putValueInSharePreference(String key, Object value) {
        if (value instanceof String)
            sharedPreferences.edit().putString(key, (String) value).apply();
        else if (value instanceof Integer)
            sharedPreferences.edit().putInt(key, (int) value).apply();
        else if (value instanceof Float)
            sharedPreferences.edit().putFloat(key, (float) value).apply();
        else if (value instanceof Boolean)
            sharedPreferences.edit().putBoolean(key, (boolean) value).apply();
        else if (value instanceof Long)
            sharedPreferences.edit().putLong(key, (int) value).apply();
    }

    public interface OnSettingsChangeListener {

        void onSettingChange(String key, Object value);
    }
}
