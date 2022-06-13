package com.example.steammarketbot.core.logic;

import android.util.Pair;

import com.example.steammarketbot.core.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Request {

    public static int STRONG_PERSISTENCE = 1;
    public static int WEAK_PERSISTENCE = 0;

    private String host = "";
    private String path = "/";
    private String data = "";
    private String parameters = "";
    private String anchor = "";
    private boolean changeReferer = true;
    private final List<Pair<String, String>> additionalHeaders;
    private long requestDelay = 0;
    private int randomRate = 0;
    private int persistence = 1;

    public Request(String host) {
        this.host = host;
        additionalHeaders = new ArrayList<>();
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getData() {
        return data;
    }

    public String getData(String key) {
        String value;
        value = Strings.search(data, key + "=", "&", false);
        if (value.equals(""))
            value = Strings.search(data, key + "=", "&", false);
        return value;
    }

    public String getParameters() {
        return parameters;
    }

    public String getAnchor() {
        return anchor;
    }

    public boolean isChangeReferer() {
        return changeReferer;
    }

    public List<Pair<String, String>> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public long getRequestDelay() {
        return requestDelay;
    }

    public int getRandomRate() {
        return randomRate;
    }

    public int getPersistence() {
        return persistence;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void putData(String key, Object value) {
        String mData = key + "=" + value;
        if (data.equals(""))
            data = mData;
        else
            data = data + "&" + mData;
    }

    public void putParameters(String key, Object value) {
        String mParameters = key + "=" + value;
        if (parameters.equals(""))
            parameters = mParameters;
        else
            parameters = parameters + "&" + mParameters;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public void dontChangeReferer() {
        changeReferer = false;
    }

    public void addAdditionalHeader(String key, String value) {
        additionalHeaders.add(new Pair<>(key, value));
    }

    public void setRequestDelay(long requestDelay) {
        this.requestDelay = requestDelay;
    }

    public void setRandomRate(int randomRate) {
        this.randomRate = randomRate;
    }

    public void setPersistence(int persistence) {
        if (persistence < 0)
            this.persistence = 0;
        else if (persistence > 1)
            this.persistence = 1;
        else
            this.persistence = persistence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        Request request = (Request) o;
        return changeReferer == request.changeReferer && requestDelay == request.requestDelay && randomRate == request.randomRate && persistence == request.persistence && Objects.equals(host, request.host) && Objects.equals(path, request.path) && Objects.equals(data, request.data) && Objects.equals(parameters, request.parameters) && Objects.equals(anchor, request.anchor) && Objects.equals(additionalHeaders, request.additionalHeaders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, path, data, parameters, anchor, changeReferer, additionalHeaders, requestDelay, randomRate, persistence);
    }
}
