package com.example.steammarketbot.core.httpsClient;

import com.example.steammarketbot.core.logic.Request;

import java.util.ArrayList;

import okhttp3.Call;

public class HttpClientResponse {

    private Call call;
    private final Request request;
    private int attempt;
    private boolean success;
    private String response;
    private boolean mainResponse = false;

    public HttpClientResponse(Request request) {
        this.request = request;
        attempt = 0;
        success = false;
        response = "";
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public Call getCall() {
        return call;
    }

    public void countAttempt() {
        attempt++;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public void setSuccess() {
        success = true;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public int getAttempt() {
        return attempt;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResponse() {
        return response;
    }

    public boolean isMainResponse() {
        return mainResponse;
    }

    public void setMainResponse() {
        mainResponse = true;
    }

    public static HttpClientResponse getMainResponse(ArrayList<HttpClientResponse> httpClientResponses) {
        if (httpClientResponses == null)
            return new HttpClientResponse(null);
        for (HttpClientResponse httpClientResponse : httpClientResponses) {
            if (httpClientResponse.isMainResponse())
                return httpClientResponse;
        }
        return httpClientResponses.get(0);
    }
}
