package com.example.steammarketbot.core.instructions.loginInstructions;


import com.example.steammarketbot.core.Strings;
import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.instructions.Instruction;
import com.example.steammarketbot.core.instructions.InstructionResult;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class LoginInstruction extends Instruction {

    //Получаем от пользователя
    private final String login;
    private final String password;
    private final String twoFactorCode;

    //Получаем от сервера
    private String publicKeyExp;
    private String publicKeyMod;
    private String rsatimestamp;
    private String tokenGid;
    private String auth;
    private String steamid;
    private String token_secure;
    private ArrayList<String> transferUrls;
    String sessionId = null;

    //Ключи данных запросов и ответов
    private final String DONOTCACHE = "donotcache";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String PUBLICKEY_EXP = "publickey_exp";
    private final String PUBLICKEY_MOD = "publickey_mod";
    private final String SUCCESS = "success";
    private final String RSATIMESTAMP = "rsatimestamp";
    private final String TIMESTAMP = "timestamp";
    private final String TOKEN_GID = "token_gid";
    private final String MESSAGE = "message";
    private final String TWOFACTORCODE = "twofactorcode";
    private final String EMAILAUTH = "emailauth";
    private final String LOGINFRIENDLYNAME = "loginfriendlyname";
    private final String CAPTCHAGID = "captchagid";
    private final String CAPTCHA_TEXT = "captcha_text";
    private final String EMAILSTEAMID = "emailsteamid";
    private final String REMEMBER_LOGIN = "remember_login";
    private final String TOKENTYPE = "tokentype";
    private final String REQUIRES_TWOFACTOR = "requires_twofactor";
    private final String LOGIN_COMPLETE = "login_complete";
    private final String AUTH = "auth";
    private final String STEAMID = "steamid";
    private final String TOKEN_SECURE = "token_secure";
    private final String TRANSFER_URLS = "transfer_urls";

    public LoginInstruction(long time,
                            String instructionDescription,
                            String login,
                            String password,
                            String twoFactorCode) {
        super(time, PRIORITY_LOGIN, 15000, instructionDescription);
        this.login = login;
        this.password = password;
        this.twoFactorCode = twoFactorCode;
        transferUrls = new ArrayList<>();
    }

    @Override
    public RequestList execute() {
        superInstructionExecute();
        request = new Request(host);
        requestList = new RequestList();
        request.setPath("/login/home/");
        request.putParameters("goto", "market%2F");
        requestList.add(request);
        return requestList;
    }

    @Override
    public RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, IOException, ScriptException, NoSuchMethodException {
        JSONObject json;
        superInstructionStep(HttpClientResponse.getMainResponse(httpClientResponses).getResponse());
        String response = HttpClientResponse.getMainResponse(httpClientResponses).getResponse();
        request = new Request(host);
        requestList = new RequestList();

        switch (getNextStep()) {
            case 1:
                if (isLoggedIn()) {
                    setFinish();
                    instructionResult.setSessionId(sessionId);
                    instructionResult.setSuccess();
                    return requestList;
                }
                request.setPath("/login/getrsakey/");
                request.putData(DONOTCACHE, System.currentTimeMillis());
                request.dontChangeReferer();
                request.putData(USERNAME, login);
                requestList.add(request);
                setNextStep(2);
                break;

            case 2:
                json = new JSONObject(response);
                if (!json.getBoolean(SUCCESS)) {
                    setFinish();
                    instructionResult = new InstructionResult();
                    instructionResult.setSessionId(sessionId);
                    instructionResult.setMessage(json.getString(MESSAGE));
                    setFinish();
                    break;
                }
                publicKeyExp = json.getString(PUBLICKEY_EXP);
                publicKeyMod = json.getString(PUBLICKEY_MOD);
                rsatimestamp = json.getString(TIMESTAMP);
                tokenGid = json.getString(TOKEN_GID);
                request.setPath("/login/dologin/");
                request.putData(DONOTCACHE, System.currentTimeMillis());
                request.putData(PASSWORD, encryptPassword());
                request.putData(USERNAME, login);
                request.putData(TWOFACTORCODE, twoFactorCode);
                request.putData(EMAILAUTH, "");
                request.putData(LOGINFRIENDLYNAME, "");
                request.putData(CAPTCHAGID, "");
                request.putData(CAPTCHA_TEXT, "");
                request.putData(EMAILSTEAMID, "");
                request.putData(RSATIMESTAMP, rsatimestamp);
                request.putData(REMEMBER_LOGIN, true);
                request.putData(TOKENTYPE, -1);
                requestList.add(request);
                setNextStep(3);
                break;

            case 3:
                json = new JSONObject(response);
                if (!json.getBoolean(SUCCESS)) {
                    setFinish();
                    if (json.getBoolean(TWOFACTORCODE)) {
                        instructionResult = new InstructionResult();
                        instructionResult.setSessionId(sessionId);
                        instructionResult.setCode(InstructionResult.CODE_TWO_FACTOR_AUTH_INCORRECT);
                        break;
                    }
                    else {
                        instructionResult = new InstructionResult();
                        instructionResult.setSessionId(sessionId);
                        instructionResult.setSuccess();
                        instructionResult.setMessage(json.getString(MESSAGE));
                        break;
                    }
                }
                else {
                    if (json.getBoolean(LOGIN_COMPLETE)) {
                        auth = json.getString(AUTH);
                        steamid = json.getString(STEAMID);
                        token_secure = json.getString(TOKEN_SECURE);
                        int length = json.getJSONArray(TRANSFER_URLS).length();
                        for (int i = 0; i < length; i++) {
                            transferUrls.add(json.getJSONArray(TRANSFER_URLS).getString(i));
                        }
                        URL url = new URL(transferUrls.get(0));
                        request.setHost(url.getHost());
                        request.setPath(url.getPath());
                        request.putData(STEAMID, steamid);
                        request.putData(TOKEN_SECURE, token_secure);
                        request.putData(AUTH, auth);
                        request.putData(REMEMBER_LOGIN, true);
                        request.setRequestDelay(1);
                        request.dontChangeReferer();
                        requestList.add(request);

                        request = new Request(host);
                        url = new URL(transferUrls.get(1));
                        request.setHost(url.getHost());
                        request.setPath(url.getPath());
                        request.putData(STEAMID, steamid);
                        request.putData(TOKEN_SECURE, token_secure);
                        request.putData(AUTH, auth);
                        request.putData(REMEMBER_LOGIN, true);
                        request.setRequestDelay(1);
                        request.dontChangeReferer();
                        requestList.add(request);

                        request = new Request(host);
                        request.setPath("/my/goto");
                        request.setRequestDelay(1);
                        requestList.add(request);
                        requestList.setStepDelay(1);
                    } else {
                        instructionResult = new InstructionResult();
                        instructionResult.setSessionId(sessionId);
                        instructionResult.setMessage(json.getString(MESSAGE));
                    }
                }
                setNextStep(4);
                break;

            case 4:
                request.setPath("/market/");
                requestList.add(request);
                setNextStep(5);
                break;

            case 5:
                Elements scripts = document.getElementsByTag("script");
                for (Element element: scripts) {
                    if (element.text().contains("g_sessionID")) {
                        sessionId = Strings.search(
                                element.text(),
                                "g_sessionID = \"",
                                "\"");
                        setFinish();
                        instructionResult = new InstructionResult();
                        instructionResult.setSessionId(sessionId);
                        instructionResult.setSuccess();
                        break;
                    }
                }
                setFinish();
                instructionResult = new InstructionResult();
                instructionResult.setSessionId(sessionId);
                instructionResult.setSuccess();
                instructionResult.setCode(InstructionResult.SESSION_ID_NOT_FOUND);
                break;
        }
        return requestList;
    }

    @Override
    public InstructionResult getInstructionResult() {
        return instructionResult;
    }

    private String encryptPassword() throws ScriptException, NoSuchMethodException {
        ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("rhino");
        Invocable invocable = (Invocable) scriptEngine;
        scriptEngine.eval(JavaScripts.RSA);
        scriptEngine.eval(JavaScripts.BIG_INTEGER);
        Object RSA = scriptEngine.get("RSA");
        Object publicKey = invocable.invokeMethod(RSA, "getPublicKey", publicKeyMod, publicKeyExp);
        Object encryptedPassword = invocable.invokeMethod(RSA, "encrypt", password, publicKey);
        return encryptedPassword.toString();
    }
}