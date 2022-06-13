package com.example.steammarketbot.core.instructions.mainPage;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;
import com.example.steammarketbot.core.profileInfo.lot.LotForBuy;
import com.example.steammarketbot.core.profileInfo.lot.LotForSell;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;

import javax.script.ScriptException;

public class CancelListingOrBuyOrderInstruction extends MainPageInstruction {

    private HashSet<LotForSell> lotForSellSet;
    private HashSet<LotForBuy> lotForBuySet;

    protected CancelListingOrBuyOrderInstruction(long timeWindowStarts,
                                                 long timeWindowEnds,
                                                 int priority,
                                                 long instructionTimeout,
                                                 HashSet<LotForSell> lotForSellSet,
                                                 HashSet<LotForBuy> lotForBuySet,
                                                 String instructionDescription,
                                                 long walletExpiresTime) {
        super(
                timeWindowStarts,
                timeWindowEnds,
                priority,
                instructionTimeout,
                instructionDescription,
                walletExpiresTime);
        this.lotForSellSet = lotForSellSet;
        this.lotForBuySet = lotForBuySet;
    }

    @Override
    public RequestList execute() {
        superInstructionExecute();
        request = new Request(host);
        requestList = new RequestList();
        for (LotForSell lotForSell: lotForSellSet) {
            requestList.add(getCancelListingRequest(lotForSell));
        }
        for (LotForSell lotForSell: lotForSellSet) {
            requestList.add(getCancelListingRequest(lotForSell));
        }
        setNextStep(1);
        return requestList;
    }

    @Override
    public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, IOException, ScriptException, NoSuchMethodException, ParseException {
        request = new Request(host);
        requestList = new RequestList();

        switch (getNextStep()) {

            case 1:
                for (HttpClientResponse httpClientResponse: httpClientResponses) {
                    if (httpClientResponse.isSuccess()) {
                        requestList.add(httpClientResponse.getRequest());
                    }
                }
                setFinish();
        }

        return requestList;
    }
}
