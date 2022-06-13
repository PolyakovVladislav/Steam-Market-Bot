package com.example.steammarketbot.core.status;

import android.content.res.Resources;

import com.example.steammarketbot.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SteamMarketBotStatus {

    private boolean netConnected;
    private boolean pause;
    private boolean loggedIn;

    private boolean session;
    private long sessionStarted;
    private long workSessionLength;
    private int randomRateForWorkSessionLength;
    private long sessionEndsMin;
    private long sessionEndsMax;
    private long pauseLength;
    private int randomRateForPauseLength;
    private long sessionEnded;
    private long sessionStartsMin;
    private long sessionStartsMax;

    private boolean pauseErrors;
    private long pauseErrorsStarted;
    private long pauseInCaseOfErrorToMuchRequestsLength;
    private int randomRateForPauseInCaseOfToMuchRequests;
    private long errorsPauseEndsMin;
    private long errorsPauseEndsMax;

    private int status;
    private String titleStatus;
    private String statusDescription;
    private final StatusChangeListener statusChangeListener;

    private final Resources resources;

    public static final int STATUS_WORKING = 0;
    public static final int STATUS_PAUSED = 1;
    public static final int STATUS_AUTO_PAUSED = 2;
    public static final int STATUS_PAUSED_ERRORS = 3;
    public static final int STATUS_STATUS_AWAITING_LOGIN = 4;
    public static final int STATUS_NOT_CONNECTED = 5;

    public SteamMarketBotStatus(Resources resources, StatusChangeListener statusChangeListener,
                                long workSessionLength, int randomRateForWorkSessionLength,
                                long pauseLength, int randomRateForPauseLength,
                                long pauseInCaseOfErrorToMuchRequestsLength,
                                int randomRateForPauseInCaseOfToMuchRequests) {
        pause = false;
        loggedIn = false;
        session = false;
        pauseErrors = false;
        this.resources = resources;
        this.statusChangeListener = statusChangeListener;
        setWorkSessionLength(workSessionLength);
        setRandomRateForWorkSessionLength(randomRateForWorkSessionLength);
        setPauseLength(pauseLength);
        setRandomRateForPauseLength(randomRateForPauseLength);
        setPauseInCaseOfErrorToMuchRequestsLength(pauseInCaseOfErrorToMuchRequestsLength);
        setRandomRateForPauseInCaseOfToMuchRequests(randomRateForPauseInCaseOfToMuchRequests);
        setStatus();
    }

    public void setWorkSessionLength(long workSessionLength) {
        this.workSessionLength = workSessionLength;
        sessionEndsMin = sessionStarted + workSessionLength - workSessionLength * randomRateForWorkSessionLength / 100;
        sessionEndsMax = sessionStarted + workSessionLength - workSessionLength * randomRateForWorkSessionLength / 100;
        statusChangeListener.onSessionLengthChanged(sessionEndsMin, sessionEndsMax);
    }

    public void setRandomRateForWorkSessionLength(int randomRateForWorkSessionLength) {
        this.randomRateForWorkSessionLength = randomRateForWorkSessionLength;
        sessionEndsMin = sessionStarted + workSessionLength - workSessionLength * randomRateForWorkSessionLength / 100;
        sessionEndsMax = sessionStarted + workSessionLength - workSessionLength * randomRateForWorkSessionLength / 100;
        statusChangeListener.onSessionLengthChanged(sessionEndsMin, sessionEndsMax);
    }

    public void setPauseLength(long pauseLength) {
        this.pauseLength = pauseLength;
        sessionStartsMin = sessionEnded + pauseLength - pauseLength * randomRateForPauseLength / 100;
        sessionStartsMax = sessionEnded + pauseLength - pauseLength * randomRateForPauseLength / 100;
        setStatus();
        statusChangeListener.onSessionPauseLengthChanged(sessionStartsMin, sessionStartsMax);
    }

    public void setRandomRateForPauseLength(int randomRateForPauseLength) {
        this.randomRateForPauseLength = randomRateForPauseLength;
        sessionStartsMin = sessionEnded + pauseLength - pauseLength * randomRateForPauseLength / 100;
        sessionStartsMax = sessionEnded + pauseLength - pauseLength * randomRateForPauseLength / 100;
        setStatus();
        statusChangeListener.onSessionPauseLengthChanged(sessionStartsMin, sessionStartsMax);
    }

    public void setPauseInCaseOfErrorToMuchRequestsLength(long pauseInCaseOfErrorToMuchRequestsLength) {
        this.pauseInCaseOfErrorToMuchRequestsLength = pauseInCaseOfErrorToMuchRequestsLength;
        pauseErrorsStarted = System.currentTimeMillis();
        errorsPauseEndsMin = pauseErrorsStarted + pauseInCaseOfErrorToMuchRequestsLength - pauseInCaseOfErrorToMuchRequestsLength * randomRateForPauseInCaseOfToMuchRequests / 100;
        errorsPauseEndsMax = pauseErrorsStarted + pauseInCaseOfErrorToMuchRequestsLength - pauseInCaseOfErrorToMuchRequestsLength * randomRateForPauseInCaseOfToMuchRequests / 100;
        setStatus();
        statusChangeListener.onPauseErrorsLengthChanged(errorsPauseEndsMin, errorsPauseEndsMax);
    }

    public void setRandomRateForPauseInCaseOfToMuchRequests(int randomRateForPauseInCaseOfToMuchRequests) {
        this.randomRateForPauseInCaseOfToMuchRequests = randomRateForPauseInCaseOfToMuchRequests;
        pauseErrorsStarted = System.currentTimeMillis();
        errorsPauseEndsMin = pauseErrorsStarted + pauseInCaseOfErrorToMuchRequestsLength - pauseInCaseOfErrorToMuchRequestsLength * randomRateForPauseInCaseOfToMuchRequests / 100;
        errorsPauseEndsMax = pauseErrorsStarted + pauseInCaseOfErrorToMuchRequestsLength - pauseInCaseOfErrorToMuchRequestsLength * randomRateForPauseInCaseOfToMuchRequests / 100;
        setStatus();
        statusChangeListener.onPauseErrorsLengthChanged(errorsPauseEndsMin, errorsPauseEndsMax);
    }

    public void setNetConnected(boolean connected) {
        if (!netConnected && connected)
            statusChangeListener.onConnectionRetrieve();
        if (netConnected && !connected)
            statusChangeListener.onConnectionLost();
        netConnected = connected;
    }

    public boolean isNetConnected() {
        return netConnected;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn() {
        loggedIn = true;
        pause = true;
        session = false;
        pauseErrors = false;
        setStatus();
        statusChangeListener.onLoggedIn();
    }

    public void setLoggedOut() {
        loggedIn = false;
        setStatus();
        statusChangeListener.onLoggedOut();
    }

    public void setPause() {
        pause = true;
        setStatus();
        statusChangeListener.onPause();
    }

    public void setResume() {
        setSessionOn();
        pauseErrors = false;
        pause = false;
        setStatus();
        statusChangeListener.onResume();
    }

    public boolean isPause() {
        return pause;
    }

    public void setSessionOn() {
        session = true;
        sessionStarted = System.currentTimeMillis();
        sessionEndsMin = sessionStarted + workSessionLength - workSessionLength * randomRateForWorkSessionLength / 100;
        sessionEndsMax = sessionStarted + workSessionLength - workSessionLength * randomRateForWorkSessionLength / 100;
        setStatus();
        statusChangeListener.onSessionStarts(sessionEndsMin, sessionEndsMax);
    }

    public void setSessionOff() {
        session = false;
        sessionEnded = System.currentTimeMillis();
        sessionStartsMin = sessionEnded + pauseLength - pauseLength * randomRateForPauseLength / 100;
        sessionStartsMax = sessionEnded + pauseLength - pauseLength * randomRateForPauseLength / 100;
        setStatus();
        statusChangeListener.onSessionEnds(sessionStartsMin, sessionStartsMax);
    }

    public boolean isSessionOn() {
        return session;
    }

    public void setPauseErrors(boolean pauseErrors) {
        this.pauseErrors = pauseErrors;
        if (pauseErrors) {
            pauseErrorsStarted = System.currentTimeMillis();
            errorsPauseEndsMin = pauseErrorsStarted + pauseInCaseOfErrorToMuchRequestsLength - pauseInCaseOfErrorToMuchRequestsLength * randomRateForPauseInCaseOfToMuchRequests / 100;
            errorsPauseEndsMax = pauseErrorsStarted + pauseInCaseOfErrorToMuchRequestsLength - pauseInCaseOfErrorToMuchRequestsLength * randomRateForPauseInCaseOfToMuchRequests / 100;
            setStatus();
            statusChangeListener.onPauseErrors(errorsPauseEndsMin, errorsPauseEndsMax);
        }
    }

    public boolean isPauseErrors() {
        return pauseErrors;
    }

    public String getTitleStatus() {
        return titleStatus;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus() {
        titleStatus = "";
        statusDescription = "";
        if (netConnected) {
            if (loggedIn) {
                if (pause) {
                    status = STATUS_PAUSED;
                    titleStatus = resources.getString(R.string.status_paused);
                } else {
                    SimpleDateFormat simpleDateFormat;
                    long timeOffsetMin;
                    long timeOffsetMax;
                    if (pauseErrors) {
                        status = STATUS_PAUSED_ERRORS;
                        titleStatus = resources.getString(R.string.status_pause_error);
                        statusDescription = resources.getString(R.string.status_description_session_on_pause);
                        timeOffsetMin = errorsPauseEndsMin;
                        timeOffsetMax = errorsPauseEndsMax;
                    } else if (!session) {
                        status = STATUS_AUTO_PAUSED;
                        titleStatus = resources.getString(R.string.status_auto_pause);
                        statusDescription = resources.getString(R.string.status_description_session_on_pause);
                        timeOffsetMin = sessionStartsMin;
                        timeOffsetMax = sessionStartsMax;
                    } else {
                        status = STATUS_WORKING;
                        titleStatus = resources.getString(R.string.status_working);
                        statusDescription = resources.getString(R.string.status_description_next_session_pause);
                        timeOffsetMin = sessionEndsMin;
                        timeOffsetMax = sessionEndsMin;
                    }
                    simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String timeMin = simpleDateFormat.format(new Date(System.currentTimeMillis()
                            + timeOffsetMin));
                    String timeMax = simpleDateFormat.format(new Date(System.currentTimeMillis()
                            + timeOffsetMax));
                    statusDescription = " " + timeMin + " - " + timeMax;
                }
            }
            else
            {
                status = STATUS_STATUS_AWAITING_LOGIN;
                titleStatus = resources.getString(R.string.status_awaiting_login);
            }
        }
        else {
            status = STATUS_NOT_CONNECTED;
            titleStatus = resources.getString(R.string.status_awaiting_internet);
        }
        statusChangeListener.onStatusDescriptionChanged(titleStatus, statusDescription);
    }
}
