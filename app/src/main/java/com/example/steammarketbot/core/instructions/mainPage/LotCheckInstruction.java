package com.example.steammarketbot.core.instructions.mainPage;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;
import com.example.steammarketbot.core.profileInfo.lot.LotForSellList;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;


public class LotCheckInstruction extends MainPageInstruction{

    protected LotCheckInstruction(long timeWindowStarts,
                                  long timeWindowEnds,
                                  long instructionTimeout,
                                  String instructionDescription,
                                  long walletExpiresTime) {
        super(timeWindowStarts,
                timeWindowEnds,
                PRIORITY_CHECK_ITEMS,
                instructionTimeout,
                instructionDescription,
                walletExpiresTime);
    }

    @Override
    public RequestList execute() {
        superMainPageExecute();
        request.setPath("/market/");
        requestList.add(request);
        return requestList;
    }

    @Override
    public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, ParseException {
        superInstructionStep(HttpClientResponse.getMainResponse(httpClientResponses).getResponse());
        JSONObject json;
        request = new Request(host);
        requestList = new RequestList();

        switch (getNextStep()) {
            case 1:
                int sellingItemsPagesCount = getListingsPagesCount();
                if (!isMainPage())
                    setFinish();
                else if (getListingsCountPerPage() != 100) {
                    request.setPath("/market/");
                    requestList.add(request);
                    setCookieListingCount100();
                    setNextStep(1);
                } else {
                    for (int i = 1; sellingItemsPagesCount > i; i++) {
                        request = getRequestForItemsPage(i);
                        request.setRequestDelay(1);
                        request.dontChangeReferer();
                        requestList.add(request);
                    }
                }
                if (sellingItemsPagesCount == 1) {
                    instructionResult.setSuccess();
                    setFinish();
                }
                setNextStep(2);
                break;

            case 2:
                for (HttpClientResponse httpClientResponse : httpClientResponses) {
                    if (httpClientResponse.isSuccess()) {
                        json = new JSONObject(httpClientResponse.getResponse());
                        lotForSellSet.addAll(parseJsonResponse(json));
                    } else {
                        requestList.add(httpClientResponse.getRequest());
                    }
                    requestList.setStepDelay(1);
                    if (requestList.size() == 0) {
                        instructionResult.setSuccess();
                        instructionResult.setSellLotList(
                                new LotForSellList(
                                        lotForSellSet,
                                        System.currentTimeMillis()));
                        setFinish();
                    }
                }
                setNextStep(3);
                break;

            case 3:
                for (HttpClientResponse httpClientResponse : httpClientResponses) {
                    if (httpClientResponse.isSuccess()) {
                        json = new JSONObject(httpClientResponse.getResponse());
                        lotForSellSet.addAll(parseJsonResponse(json));
                    }
                }
                instructionResult.setSuccess();
                instructionResult.setSellLotList(
                        new LotForSellList(
                                lotForSellSet,
                                System.currentTimeMillis()));
                setFinish();
                break;
        }
        return requestList;
    }
}