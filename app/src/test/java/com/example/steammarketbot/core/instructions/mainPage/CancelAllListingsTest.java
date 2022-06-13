package com.example.steammarketbot.core.instructions.mainPage;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;
import com.example.steammarketbot.core.testHTML.TestResponse;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.script.ScriptException;

public class CancelAllListingsTest {

    static CancelAllListingsInstruction cancelAllListingsInstruction;
    static long testStarted;
    static String host = "steamcommunity.com";

    @Before
    public void setUp() {
        testStarted = System.currentTimeMillis();
        cancelAllListingsInstruction = new CancelAllListingsInstruction(
                testStarted + 60 * 1000,
                testStarted + 120 * 1000,
                CancelAllListingsInstruction.PRIORITY_REMOVE_LISTING,
                60 * 1000,
                "Отмена всех продаж",
                testStarted + 90 * 1000
        );
    }

    @Test
    public void test_case10ItemsPerPage() throws ScriptException, JSONException, IOException, ParseException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        RequestList requestList;
        ArrayList<HttpClientResponse> httpClientResponses = new ArrayList<>();
        HttpClientResponse httpClientResponse;
        requestList = cancelAllListingsInstruction.execute();
        Assert.assertEquals(createExpectedRequestListForStep0(), requestList);

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.MAIN_PAGE_1));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        Assert.assertEquals(
                createExpectedRequestListForStep1_Case_10ItemsPerPage(
                        requestList),
                requestList);
        httpClientResponses.clear();

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.MAIN_PAGE_4));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        httpClientResponses.clear();
        Assert.assertEquals(101, requestList.size());


        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.JSON_MY_LISTINGS_RESPONSE1));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        httpClientResponses.clear();
        Assert.assertEquals(101, requestList.size());

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.JSON_MY_LISTINGS_RESPONSE2));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        httpClientResponses.clear();
        Assert.assertEquals(101, requestList.size());

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.JSON_MY_LISTINGS_RESPONSE3));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        httpClientResponses.clear();
        Assert.assertEquals(88, requestList.size());
    }

    @Test
    public void test_case100ItemsPerPage() throws ScriptException, JSONException, IOException, ParseException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        RequestList requestList;
        ArrayList<HttpClientResponse> httpClientResponses = new ArrayList<>();
        HttpClientResponse httpClientResponse;
        requestList = cancelAllListingsInstruction.execute();
        Assert.assertEquals(createExpectedRequestListForStep0(), requestList);

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.MAIN_PAGE_4));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        Assert.assertEquals(101, requestList.size());
        httpClientResponses.clear();


        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.JSON_MY_LISTINGS_RESPONSE1));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        httpClientResponses.clear();
        Assert.assertEquals(101, requestList.size());

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.JSON_MY_LISTINGS_RESPONSE2));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        httpClientResponses.clear();
        Assert.assertEquals(101, requestList.size());

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.JSON_MY_LISTINGS_RESPONSE3));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        httpClientResponses.clear();
        Assert.assertEquals(88, requestList.size());
    }

    @Test
    public void test_caseTotal9Items_case10ItemsPerPage() throws ScriptException, JSONException, IOException, ParseException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        RequestList requestList;
        ArrayList<HttpClientResponse> httpClientResponses = new ArrayList<>();
        HttpClientResponse httpClientResponse;
        requestList = cancelAllListingsInstruction.execute();
        Assert.assertEquals(createExpectedRequestListForStep0(), requestList);

        httpClientResponse = new HttpClientResponse(requestList.getNext());
        httpClientResponse.setResponse(TestResponse.getResponse(TestResponse.MAIN_PAGE_3));
        httpClientResponses.add(httpClientResponse);
        requestList = cancelAllListingsInstruction.getRequestList(httpClientResponses);
        Assert.assertEquals(10, requestList.size());
        httpClientResponses.clear();

    }

    private RequestList createExpectedRequestListForStep0() {
        Request request = new Request(host);
        request.setPath("/market/");
        RequestList requestList = new RequestList();
        requestList.addMain(request);
        return requestList;
    }

    private RequestList createExpectedRequestListForStep1_Case_10ItemsPerPage(RequestList actualRequestList) throws NoSuchFieldException, IllegalAccessException {
        Request request = new Request(host);
        request.setPath("/market/");
        RequestList requestList = new RequestList();
        requestList.addMain(request);
        requestList.putAdditionalCookies(
                "steamcommunity.com",
                "/market",
                "ActListPageSize",
                "100",
                Long.parseLong(actualRequestList
                .getAdditionalCookies()
                .get(0)
                .get("key_cookie_expires_at")));
        return requestList;
    }

    private RequestList createExpectedRequestListForStep1_Case_100ItemsPerPage(RequestList actualRequestList) throws NoSuchFieldException, IllegalAccessException {
        Request request = new Request(host);
        request.setPath("/market/");
        RequestList requestList = new RequestList();
        requestList.addMain(request);
        return requestList;
    }
}