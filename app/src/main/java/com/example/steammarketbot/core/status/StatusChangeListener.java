package com.example.steammarketbot.core.status;

public interface StatusChangeListener {

    void onLoggedIn();
    void onLoggedOut();
    void onPause();
    void onResume();
    void onSessionStarts(long sessionEndsMin, long sessionEndsMax);
    void onSessionLengthChanged(long sessionEndsMin, long sessionEndsMax);
    void onSessionEnds(long sessionStartsMin, long sessionStartsMax);
    void onSessionPauseLengthChanged(long sessionStartsMin, long sessionStartsMax);
    void onPauseErrors(long pauseErrorsEndsMin, long pauseErrorsEndsMax);
    void onPauseErrorsLengthChanged(long pauseErrorsEndsMin, long pauseErrorsEndsMax);
    void onStatusDescriptionChanged(String titleStatus, String statusDescription);
    void onConnectionRetrieve();
    void onConnectionLost();
}
