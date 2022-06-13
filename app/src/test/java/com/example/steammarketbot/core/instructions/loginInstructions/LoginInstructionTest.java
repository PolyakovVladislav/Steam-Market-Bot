package com.example.steammarketbot.core.instructions.loginInstructions;

import static org.junit.Assert.assertEquals;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;
import com.example.steammarketbot.core.testHTML.TestResponse;

import org.json.JSONException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import javax.script.ScriptException;

public class LoginInstructionTest {

    static LoginInstruction loginInstruction;
    static RequestList actualRequestList;
    static RequestList expectedRequestList;
    static Request expectedRequest;
    static HttpClientResponse httpClientResponse;
    static ArrayList<HttpClientResponse> httpClientResponsesList;

    private static final String DONOTCACHE = "donotcache";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String PUBLICKEY_EXP = "publickey_exp";
    private static final String PUBLICKEY_MOD = "publickey_mod";
    private static final String SUCCESS = "success";
    private static final String RSATIMESTAMP = "rsatimestamp";
    private static final String TOKEN_GID = "token_gid";
    private static final String MESSAGE = "message";
    private static final String TWOFACTORCODE = "twofactorcode";
    private static final String EMAILAUTH = "emailauth";
    private static final String LOGINFRIENDLYNAME = "loginfriendlyname";
    private static final String CAPTCHAGID = "captchagid";
    private static final String CAPTCHA_TEXT = "captcha_text";
    private static final String EMAILSTEAMID = "emailsteamid";
    private static final String REMEMBER_LOGIN = "remember_login";
    private static final String TOKENTYPE = "tokentype";
    private static final String REQUIRES_TWOFACTOR = "requires_twofactor";
    private static final String LOGIN_COMPLETE = "login_complete";
    private static final String AUTH = "auth";
    private static final String STEAMID = "steamid";
    private static final String TOKEN_SECURE = "token_secure";
    private static final String TRANSFER_URLS = "transfer_urls";

    static final String host = "steamcommunity.com";
    static final String login = "wkalik1";
    static final String password = "dom348214";
    static final String twoFactorCode = "twoFactorCode";

    @BeforeClass
    public static void prepareExpectedRequest() {
        loginInstruction = new LoginInstruction(
                0,
                "Инструкция логин",
                login,
                password,
                twoFactorCode
        );
        expectedRequestList = new RequestList();
        expectedRequest = new Request(host);
        httpClientResponsesList = new ArrayList<>();
        httpClientResponse = new HttpClientResponse(null);
    }

    @Test
    public void testGetInstructionStepList_step0_caseIsLoginFalse_caseTwoFactor_Required() throws ScriptException, JSONException, IOException, NoSuchMethodException {

        actualRequestList = loginInstruction.execute();
        expectedRequest.setPath("/login/home/");
        expectedRequest.putParameters("goto", "market%2F");
        expectedRequestList.add(expectedRequest);
        assertEquals(expectedRequestList, actualRequestList);
        expectedRequestList.clear();

        prepareResponseList(TestResponse.LOGIN_PAGE);
        actualRequestList = loginInstruction.getRequestList(httpClientResponsesList);
        expectedRequest = new Request(host);
        expectedRequest.setPath("/login/getrsakey/");
        expectedRequest.putData(
                DONOTCACHE,
                actualRequestList.getNext().getData(DONOTCACHE));
        expectedRequest.dontChangeReferer();
        expectedRequest.putData(USERNAME, login);
        expectedRequestList.add(expectedRequest);
        assertEquals(expectedRequestList, actualRequestList);
        expectedRequestList.clear();

        prepareResponseList(TestResponse.RSA_JSON_RESPONSE_CASE_SUCCESS);
        actualRequestList = loginInstruction.getRequestList(httpClientResponsesList);
        expectedRequest = new Request(host);
        expectedRequest.setPath("/login/dologin/");
        expectedRequest.putData(DONOTCACHE, actualRequestList.getNext().getData(DONOTCACHE));
        expectedRequest.putData(PASSWORD, actualRequestList.getNext().getData(PASSWORD));
        expectedRequest.putData(USERNAME, login);
        expectedRequest.putData(TWOFACTORCODE, twoFactorCode);
        expectedRequest.putData(EMAILAUTH, "");
        expectedRequest.putData(LOGINFRIENDLYNAME, "");
        expectedRequest.putData(CAPTCHAGID, "");
        expectedRequest.putData(CAPTCHA_TEXT, "");
        expectedRequest.putData(EMAILSTEAMID, "");
        expectedRequest.putData(RSATIMESTAMP, actualRequestList.getNext().getData(RSATIMESTAMP));
        expectedRequest.putData(REMEMBER_LOGIN, true);
        expectedRequest.putData(TOKENTYPE, -1);
        expectedRequestList.add(expectedRequest);
        assertEquals(expectedRequestList, actualRequestList);
    }

    private void prepareResponseList(String file) throws IOException {
        httpClientResponse.setResponse(
                TestResponse.getResponse(file));
        httpClientResponsesList.add(httpClientResponse);
    }
}