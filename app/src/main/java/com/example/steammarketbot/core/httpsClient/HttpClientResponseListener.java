package com.example.steammarketbot.core.httpsClient;

public interface HttpClientResponseListener {

    void onClientError(HttpClientResponse httpClientResponse);
    void onConnectionFailed(HttpClientResponse httpClientResponse);
    void onSuccess(HttpClientResponse httpClientResponse);
}
