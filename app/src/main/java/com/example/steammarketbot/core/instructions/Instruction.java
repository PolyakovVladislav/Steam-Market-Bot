package com.example.steammarketbot.core.instructions;

import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.logic.Request;
import com.example.steammarketbot.core.logic.RequestList;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.script.ScriptException;

public abstract class Instruction {

    public static int PRIORITY_LOGIN = 5;
    public static int PRIORITY_USER = 4;
    public static int PRIORITY_REMOVE_LISTING = 3;
    public static int PRIORITY_ANALISE = 2;
    public static int PRIORITY_SELL = 1;
    public static int PRIORITY_CHECK_ITEMS = 0;

    protected final String host = "steamcommunity.com";

    private static int counter = 0;

    private int nextStep;
    private int attempt;
    private final long instructionTimeout;
    private long timeWindowStarts;
    private long timeWindowEnds;
    private final int priority;
    private final int id;
    private final String instructionDescription;
    private boolean finish;

    protected Document document;

    protected Request request;
    protected RequestList requestList;
    protected InstructionResult instructionResult;

    protected Instruction(long time, int priority,
                          long instructionTimeout, String instructionDescription) {
        nextStep = 0;
        timeWindowStarts = time;
        timeWindowEnds = time;
        this.priority = priority;
        this.instructionTimeout = instructionTimeout;
        attempt = 0;
        id = counter;
        counter++;
        this.instructionDescription = instructionDescription;
        instructionResult = new InstructionResult();
    }

    protected Instruction(long timeWindowStarts, long timeWindowEnds, int priority,
                          long instructionTimeout, String instructionDescription) {
        nextStep = 0;
        this.timeWindowStarts = timeWindowStarts;
        this.timeWindowEnds = timeWindowEnds;
        this.priority = priority;
        this.instructionTimeout = instructionTimeout;
        attempt = 0;
        id = counter;
        counter++;
        this.instructionDescription = instructionDescription;
        instructionResult = new InstructionResult();
    }

    public abstract RequestList execute();
    public abstract RequestList getRequestList(ArrayList<HttpClientResponse> httpClientResponses) throws JSONException, IOException, ScriptException, NoSuchMethodException, ParseException;
    public InstructionResult getInstructionResult() {
        return instructionResult;
    }

    public void superInstructionExecute() {
        nextStep = 1;
        finish = false;
    }

    protected int getNextStep() {
       return nextStep;
    }

    public long getTimeWindowStarts() {
       return timeWindowStarts;
    }

    public long getTimeWindowEnds() {
        return timeWindowEnds;
    }

    public String getInstructionDescription() {
        return instructionDescription;
    }

    public void setTime(long timeWindowsStarts, long timeWindowsEnds) {
        this.timeWindowStarts = timeWindowsStarts;
        this.timeWindowEnds = timeWindowsEnds;
    }

    public int getPriority() {
        return priority;
    }

    public int getAttempt() {
        return attempt;
    }

    public long getInstructionTimeout() {
        return instructionTimeout;
    }

    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    public int getId() {
        return id;
    }

    protected void setNextStep(int nextStep) {
        this.nextStep = nextStep;
    }

    protected void setFinish() {
       finish = true;
    }

    public boolean isFinish() {
        return finish;
    }

    protected boolean isLoggedIn() {
        return document.getElementById("header_wallet_balance") != null;
    }

    protected void superInstructionStep(String response) {
        if (response != null) {
            if (response.contains("</html>")
                    && response.contains("</head>")
                    && response.contains("</body>"))
                document = Jsoup.parse(response);
            else
                document = Jsoup.parse("");
        }
    }
}