package com.example.steammarketbot.core.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class RequestList {

    public static final String KEY_COOKIE_DOMAIN = "key_cookie_domain";
    public static final String KEY_COOKIE_PATH = "key_cookie_path";
    public static final String KEY_COOKIE_NAME = "key_cookie_name";
    public static final String KEY_COOKIE_VALUE = "key_cookie_value";
    public static final String KEY_COOKIE_EXPIRES_AT = "key_cookie_expires_at";

    private final LinkedList<Request> requestList;
    private String requestDescription = null;
    private long stepDelay = 0;
    private int randomRate = 0;
    private boolean userDataNeeded = false;
    private Request mainRequest;
    private final ArrayList<HashMap<String, String>> additionalCookies;

    public RequestList() {
        requestList = new LinkedList<>();
        additionalCookies = new ArrayList<>();
    }

    public Request getNext() {
        return requestList.getFirst();
    }

    public void remove(Request request) {
        requestList.remove(request);
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public long getStepDelay() {
        return stepDelay;
    }

    public int getRandomRate() {
        return randomRate;
    }

    public boolean isUserDataNeeded() {
        return userDataNeeded;
    }

    public void putAdditionalCookies(String domain,
                                     String path,
                                     String name,
                                     String value,
                                     long expiresAt) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_COOKIE_DOMAIN, domain);
        map.put(KEY_COOKIE_PATH, path);
        map.put(KEY_COOKIE_NAME, name);
        map.put(KEY_COOKIE_VALUE, value);
        map.put(KEY_COOKIE_EXPIRES_AT, String.valueOf(expiresAt));
        additionalCookies.add(map);
    }

    public void clear() {
        requestList.clear();
    }

    public void add(Request request) {
        if (requestList.size() == 0)
            mainRequest = request;
        requestList.addLast(request);
    }

    public void addMain(Request request) {
        requestList.addLast(request);
        mainRequest = request;
    }

    public int size() {
        return requestList.size();
    }

    public void setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
    }

    public void setStepDelay(long stepDelay) {
        this.stepDelay = stepDelay;
    }

    public void setRandomRate(int randomRate) {
        this.randomRate = randomRate;
    }

    public void setUserDataNeeded(boolean userDataNeeded) {
        this.userDataNeeded = userDataNeeded;
    }

    public boolean isMainInstructionStep(Request request) {
        return mainRequest.equals(request);
    }

    public ArrayList<HashMap<String, String>> getAdditionalCookies() {
        return additionalCookies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestList)) return false;
        RequestList that = (RequestList) o;
        return stepDelay == that.stepDelay && randomRate == that.randomRate && userDataNeeded == that.userDataNeeded && Objects.equals(requestList, that.requestList) && Objects.equals(requestDescription, that.requestDescription) && Objects.equals(mainRequest, that.mainRequest) && Objects.equals(additionalCookies, that.additionalCookies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestList, requestDescription, stepDelay, randomRate, userDataNeeded, mainRequest, additionalCookies);
    }
}
