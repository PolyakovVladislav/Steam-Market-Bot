package com.example.steammarketbot.core.instructions.mainPage;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;
import com.example.steammarketbot.core.profileInfo.lot.LotForSell;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Set;

import javax.script.ScriptException;

public class CancelAllListingsInstruction extends MainPageInstruction {

    private int currentPage;

    protected CancelAllListingsInstruction(long timeWindowStarts,
                                           long timeWindowEnds,
                                           int priority,
                                           long instructionTimeout,
                                           String instructionDescription,
                                           long walletExpiresTime) {
        super(timeWindowStarts,
                timeWindowEnds,
                priority,
                instructionTimeout,
                instructionDescription,
                walletExpiresTime);
    }

    @Override
    public RequestList execute() {
        superMainPageExecute();
        currentPage = 1;
        request.setPath("/market/");
        requestList.addMain(request);
        return requestList;
    }

    @Override
    public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, IOException, ScriptException, NoSuchMethodException, ParseException {
        superMainPageStep(HttpClientResponse.getMainResponse(httpClientResponses).getResponse());
        JSONObject json;
        Set<LotForSell> mLotForSellSet;
        request = new Request(host);
        requestList = new RequestList();

        switch (getNextStep()) {
            case 1:
                setNextStep(2);
                if (!isMainPage())
                    setFinish();
                if (getTotalListingsCount() == 0) {
                    setFinish();
                    lotForSellSet.clear();
                    instructionResult.setSuccess();
                } else {
                    if (getListingsCountPerPage() != 100
                            && getTotalListingsCount() != getVisibleListingsCount()) {
                        request.setPath("/market/");
                        requestList.addMain(request);
                        setNextStep(1);
                    } else {
                        mLotForSellSet = getLotForSellSetFromMainPage();
                        for (LotForSell lot : mLotForSellSet) {
                            requestList.add(
                                    getCancelListingRequest(lot));
                        }

                        if (getListingsPagesCount() > 1) {
                            request = getRequestForItemsPage(currentPage);
                            requestList.addMain(request);
                        } else {
                            request.setPath("/market/");
                            requestList.addMain(request);
                            setNextStep(1);
                        }
                    }
                }
                break;

            case 2:
                setNextStep(3);
                json = new JSONObject(
                        HttpClientResponse
                                .getMainResponse(httpClientResponses)
                                .getResponse());
                mLotForSellSet = parseJsonResponse(json);
                for (LotForSell lot : mLotForSellSet) {
                    requestList.add(
                            getCancelListingRequest(lot));
                }
                int start = json.getInt(START);
                int pageSize = json.getInt(PAGE_SIZE);
                int totalCount = json.getInt(TOTAL_COUNT);
                if (start + pageSize < totalCount) {
                    request = getRequestForItemsPage(currentPage);
                    requestList.addMain(request);
                    setNextStep(2);
                }
                else {
                    request = new Request(host);
                    request.setPath("/market/");
                    requestList.addMain(request);
                }
                break;

            case 3:
                if (getTotalListingsCount() > 0)
                    setNextStep(1);
        }
        return requestList;
    }
}
