package com.example.steammarketbot.core.instructions.itemPage;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.items.Item;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.script.ScriptException;

public class ScanItemPageInstruction extends ItemPageInstruction {

    private TreeSet<Item> items;

    protected ScanItemPageInstruction(
            long timeWindowStarts,
            long timeWindowEnds,
            long instructionTimeout,
            TreeSet<Item> items,
            String instructionDescription) {
        super(
                timeWindowStarts,
                timeWindowEnds,
                PRIORITY_USER,
                instructionTimeout,
                instructionDescription);
        this.items = items;
    }

    @Override
    public RequestList execute() {
        superItemPageExecute();
        setNextStep(1);
        return getRequestListForItemsPages();
    }

    @Override
    public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, IOException, ScriptException, NoSuchMethodException, ParseException {
//        superItemPageStep();
        request = new Request(host);
        requestList = new RequestList();

        switch (getNextStep()) {

        }

        return requestList;
    }

    private RequestList getRequestListForItemsPages() {
        RequestList requestList = new RequestList();
        Request request;
        for (Item item: items) {
            if (item.getCheckedTimestamp() == 0) {
                request = new Request(host);
                request.setPath("/market/listings/"
                        + item.getAppid()
                        + "/" +
                        item.getUrlName());
                requestList.add(request);
            }
        }
        return requestList;
    }
}
