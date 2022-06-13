package com.example.steammarketbot.core.alarmManager;

public interface AlarmListener {

    void onAlarmExecuteNextInstruction();
    void onAlarmSessionOn();
    void onAlarmSessionOff();
    void onAlarmErrorsResume();
}
