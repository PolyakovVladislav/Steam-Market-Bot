package com.example.steammarketbot.core.httpsClient;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.example.steammarketbot.core.logic.Request;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpsClient implements Callback {

    public final OkHttpClient client;
    private boolean busy;
    private final HttpClientResponseListener httpClientResponseListener;
    private String userAgent;
    private String referer = "";
    private boolean changeRefererAfterResponse = true;
    private String newRefererAfterResponse = "";
    private final LinkedList<HttpClientResponse> calls;
    private final Cookies cookies;

    public HttpsClient(Context context, long callTimeout, TimeUnit timeUnit, String userAgent, HttpClientResponseListener httpClientResponseListener) {
        cookies = new Cookies();
        cookies.loadCookies(context);
        client = new OkHttpClient().newBuilder()
                .callTimeout(callTimeout, timeUnit)
                .addInterceptor(new LoggingInterceptor())
                .cookieJar(cookies)
                .connectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS))
                .build();
        busy = false;
        this.userAgent = userAgent;
        this.httpClientResponseListener = httpClientResponseListener;
        calls = new LinkedList<>();
    }

    public void sendRequest(HttpClientResponse httpClientResponse) {
        if (httpClientResponse.getRequest().getPersistence() == Request.WEAK_PERSISTENCE) {
            httpClientResponse.setAttempt(3);
        }
        calls.addLast(httpClientResponse);
        Request request = httpClientResponse.getRequest();
        busy = true;
        String url = request.getHost()
                + request.getPath();
        if (!request.getParameters().equals(""))
            url = url + "?" + request.getParameters();
        if (!request.getAnchor().equals(""))
            url = url + "#" + request.getAnchor();
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .url(url);
        if (!request.getData().equals(""))
            requestBuilder.post(
                    RequestBody.create(request.getData(),
                    MediaType.parse("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")));
        if (!this.referer.equals("")) {
            changeRefererAfterResponse = request.isChangeReferer();
            requestBuilder.addHeader("Referer", referer);
        }
        newRefererAfterResponse = "https://" + url;
        if (request.getAdditionalHeaders() != null)
            for (Pair<String, String> header: request.getAdditionalHeaders()) {
                requestBuilder.addHeader(header.first, header.second);
            }
        requestBuilder.addHeader("User-Agent", userAgent);
        calls.getLast().setCall(client.newCall(requestBuilder.build()));
        calls.getLast().getCall().enqueue(this);
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        e.printStackTrace();
        if (!call.isCanceled()) {
            for (HttpClientResponse httpClientResponse : calls) {
                if (httpClientResponse.getCall().equals(call)) {
                    if (httpClientResponse.getAttempt() >= 3) {
                        calls.remove(httpClientResponse);
                        if (calls.size() == 0)
                            busy = false;
                        httpClientResponseListener.onConnectionFailed(httpClientResponse);
                    }
                    else {
                        httpClientResponse.countAttempt();
                        call.enqueue(this);
                    }
                    return;
                }
            }
        }
        if (calls.size() == 0)
            busy = false;
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        for (HttpClientResponse httpClientResponse : calls) {
            if (httpClientResponse.getCall().equals(call)) {
                try {
                    if (!response.isRedirect()) {
                        httpClientResponse.setResponse(Objects.requireNonNull(response.body()).string());
                        calls.remove(httpClientResponse);
                        if (calls.size() == 0)
                            busy = false;
                        if (response.isSuccessful()) {
                            if (changeRefererAfterResponse)
                                referer = newRefererAfterResponse;
                            httpClientResponse.setSuccess();
                            httpClientResponseListener.onSuccess(httpClientResponse);
                        } else {
                                httpClientResponseListener.onClientError(httpClientResponse);
                            throw new IOException("Unexpected code " + response);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public int callCount() {
        return calls.size();
    }

    public boolean isBusy() {
        return busy;
    }

    public void cancelRequests() {
        for (HttpClientResponse httpClientResponse : calls) {
            if (httpClientResponse.getCall().isExecuted())
                httpClientResponse.getCall().cancel();
            else
                calls.remove(httpClientResponse);
        }
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void saveCookies(Context context) {
        cookies.saveCookies(context);
    }

    public void addCookie(String domain, String path, String name, String value,long expiresAt) {
        cookies.addCookie(domain, path, name, value, expiresAt);
    }
}
