package com.example.steammarketbot.core.instructions.loginInstructions;

import com.example.steammarketbot.core.SteamMarketBot;
import com.example.steammarketbot.core.Strings;
import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.instructions.Instruction;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;

import java.util.ArrayList;
import java.util.Objects;

public class LogoutInstruction extends Instruction {

    //Ключи данных запросов и ответов
    private final static String SESSION_ID  = "sessionid";
    private final static String IN_TRANSFER  = "in_transfer";
    private final static String AUTH  = "auth";

    protected LogoutInstruction(long time, String instructionDescription) {
        super(time, PRIORITY_LOGIN, 15000, instructionDescription);
    }

    @Override
    public RequestList execute() {
        superInstructionExecute();
        request = new Request(host);
        requestList = new RequestList();
        request.setPath("/login/logout/");
        request.putData(SESSION_ID, SteamMarketBot.getSessionId());
        requestList.add(request);
        return requestList;
    }

    @Override
    public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) {
        request = new Request(host);
        requestList = new RequestList();
        superInstructionStep(HttpClientResponse.getMainResponse(httpClientResponses).getResponse());
        String logoutScript =
                Objects.requireNonNull(document.getElementById("responsive_page_template_content")).
                        getElementsByTag("script").get(0).text();
        String in_transfer = Strings.search(logoutScript,"in_transfer: ", ",");
        String auth = Strings.search(logoutScript,"auth: \"", "\"");
        request.setHost("store.steampowered.com");
        request.setPath("/login/logout/");
        request.putData(IN_TRANSFER, in_transfer);
        request.putData(AUTH, auth);
        request.dontChangeReferer();
        requestList.add(request);

        request = new Request(host);
        request.setHost("help.steampowered.com");
        request.setPath("/login/logout/");
        request.putData(IN_TRANSFER, in_transfer);
        request.putData(AUTH, auth);
        request.dontChangeReferer();
        requestList.add(request);

        request = new Request(host);
        requestList.addMain(request);
        setFinish();
        instructionResult.setSuccess();
        return requestList;
    }
}
