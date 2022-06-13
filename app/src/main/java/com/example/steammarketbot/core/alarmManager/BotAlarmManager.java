package com.example.steammarketbot.core.alarmManager;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

public class BotAlarmManager extends BroadcastReceiver {

    public static final String ACTION_EXECUTE_NEXT_INSTRUCTION = "action_execute_next_instruction";
    public static final String ACTION_SESSION_ON= "action_session_on";
    public static final String ACTION_SESSION_OFF = "action_session_off";
    public static final String ACTION_RESUME_ERRORS  = "action_resume_errors";

    private final AlarmManager alarmManager;
    private final int timeSystemType;
    private final Context context;
    private final AlarmListener alarmListener;

    public BotAlarmManager(Context context, int timeSystemType, AlarmListener alarmListener) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.timeSystemType = timeSystemType;
        this.alarmListener = alarmListener;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_EXECUTE_NEXT_INSTRUCTION);
        intentFilter.addAction(ACTION_SESSION_ON);
        intentFilter.addAction(ACTION_SESSION_OFF);
        intentFilter.addAction(ACTION_RESUME_ERRORS);
        context.registerReceiver(this, intentFilter);
    }

    public void cancelAllAlarms() {
        cancelInstructionAlarm();
        cancelResumeErrorsAlarm();
        cancelSessionOffAlarm();
        cancelSessionOnAlarm();
    }

    public void unregisterAlarmListener() {
        context.unregisterReceiver(this);
    }

    public void setInstructionAlarm(long windowStart, long windowLength) {
        setWindowedAlarm(windowStart, windowLength, ACTION_EXECUTE_NEXT_INSTRUCTION);
    }

    public void setInstructionAlarm(long time) {
        setExactAlarm(time, ACTION_EXECUTE_NEXT_INSTRUCTION);
    }

    public void cancelInstructionAlarm() {
        cancelAlarm(ACTION_EXECUTE_NEXT_INSTRUCTION);
    }

    public void setSessionOnAlarm(long sessionStartsMin, long sessionStartsMax) {
        setWindowedAlarm(sessionStartsMin, sessionStartsMax, ACTION_SESSION_ON);
    }

    public void cancelSessionOnAlarm() {
        cancelAlarm(ACTION_SESSION_ON);
    }

    public void setSessionOffAlarm(long sessionEndsMin, long sessionEndsMax) {
        setWindowedAlarm(sessionEndsMin, sessionEndsMax, ACTION_SESSION_OFF);
    }

    public void cancelSessionOffAlarm() {
        cancelAlarm(ACTION_SESSION_OFF);
    }

    public void setResumeErrorsAlarm(long errorsPauseEndsMin, long errorsPauseEndsMax) {
        setWindowedAlarm(errorsPauseEndsMin, errorsPauseEndsMax, ACTION_RESUME_ERRORS);
    }

    public void cancelResumeErrorsAlarm() {
        cancelAlarm(ACTION_RESUME_ERRORS);
    }

    private void setWindowedAlarm(long windowStart, long windowLength, String action) {
        if (timeSystemType == AlarmManager.ELAPSED_REALTIME_WAKEUP) {
            windowStart = System.currentTimeMillis() + windowStart;
            windowLength = System.currentTimeMillis() + windowLength;
        }
        else if (timeSystemType == AlarmManager.RTC_WAKEUP) {
            windowStart = System.currentTimeMillis() + windowStart;
            windowLength = System.currentTimeMillis() + windowLength;
        }
        alarmManager.setWindow(
                timeSystemType,
                windowStart,
                windowLength,
                buildPendingIntent(action));
    }

    private void setExactAlarm(long time, String action) {
        if (timeSystemType == AlarmManager.ELAPSED_REALTIME_WAKEUP)
            time = SystemClock.elapsedRealtime() + time;
        else if (timeSystemType == AlarmManager.RTC_WAKEUP)
            time = System.currentTimeMillis() + time;
        alarmManager.setExactAndAllowWhileIdle(timeSystemType,
                time,
                buildPendingIntent(action));
    }

    private void cancelAlarm(String action) {
        alarmManager.cancel(buildPendingIntent(action));
    }

    private PendingIntent buildPendingIntent(String action) {
        Intent intent = new Intent(action);
            return PendingIntent.
                    getBroadcast(context,
                            0,
                            intent,
                            FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_EXECUTE_NEXT_INSTRUCTION:
                alarmListener.onAlarmExecuteNextInstruction();
                break;
            case ACTION_SESSION_ON:
                alarmListener.onAlarmSessionOn();
                break;
            case ACTION_SESSION_OFF:
                alarmListener.onAlarmSessionOff();
                break;
            case ACTION_RESUME_ERRORS:
                alarmListener.onAlarmErrorsResume();
                break;
        }
    }
}