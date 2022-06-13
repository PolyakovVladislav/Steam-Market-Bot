package com.example.steammarketbot.core.logic;

import android.content.Context;

import com.example.steammarketbot.core.LogicCallbackListener;
import com.example.steammarketbot.core.httpsClient.HttpClientResponse;
import com.example.steammarketbot.core.httpsClient.HttpClientResponseListener;
import com.example.steammarketbot.core.httpsClient.HttpsClient;
import com.example.steammarketbot.core.instructions.Instruction;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptException;

public class Logic implements HttpClientResponseListener {

    private final HttpsClient httpsClient;
    private Instruction instruction;
    private RequestList instructionStepList;
    private ArrayList<HttpClientResponse> httpClientResponses;
    private final LogicCallbackListener logicCallbackListener;

    private boolean busy;
    private long defaultStepDelay;
    private int defaultRandomRate;
    private int maxCountOfRequests;

    private final Timer instructionTimeOutTimer;
    private final Timer nextStepTimer;
    private final Timer nextRequestTimer;
    private final TimerTask instructionTimeOutTimerTask;
    private final TimerTask nextStepTimerTask;
    private final TimerTask nextRequestTimerTask;

    public Logic(Context context, long callTimeout, TimeUnit timeUnit,
                 String userAgent, long defaultStepDelay, int defaultRandomRate,
                 int maxCountOfRequests, LogicCallbackListener mLogicCallbackListener) {
        logicCallbackListener = mLogicCallbackListener;
        instructionTimeOutTimer = new Timer();
        nextStepTimer= new Timer();
        nextRequestTimer = new Timer();
        instructionTimeOutTimerTask = new TimerTask() {
            @Override
            public void run() {
                busy = false;
                logicCallbackListener.onInstructionTimeout(instruction.getId(), instruction.getAttempt());
            }
        };
        nextStepTimerTask = new TimerTask() {
            @Override
            public void run() {
                getInstructionStepList(httpClientResponses, false);
                sendRequest();
            }
        };
        nextRequestTimerTask = new TimerTask() {
            @Override
            public void run() {
                sendRequest();
            }
        };
        httpsClient = new HttpsClient(context, callTimeout, timeUnit, userAgent, this);
        this.defaultStepDelay = defaultStepDelay;
        this.defaultRandomRate = defaultRandomRate;
        if (maxCountOfRequests < 1)
            this.maxCountOfRequests = 1;
        else
        this.maxCountOfRequests = maxCountOfRequests;
    }

    public void setDefaultStepDelay(long defaultStepDelay) {
        this.defaultStepDelay = defaultStepDelay;
    }

    public void setDefaultRandomRate(int defaultRandomRate) {
        this.defaultRandomRate = defaultRandomRate;
    }

    public void setUserAgent(String userAgent) {
        httpsClient.setUserAgent(userAgent);
    }

    public void setMaxCountOfRequests(int maxCountOfRequests) {
        this.maxCountOfRequests = maxCountOfRequests;
    }

    public void saveCookies(Context context) {
        httpsClient.saveCookies(context);
    }

    public int getRunningInstructionId() {
        if (isBusy())
            return instruction.getId();
        else
            return -1;

    }

    public Instruction getRunningInstruction() {
        if (isBusy())
            return instruction;
        else
            return null;
    }

    public void executeInstruction(Instruction instruction) {
        busy = true;
        this.instruction = instruction;
        httpClientResponses = new ArrayList<>();
        instructionTimeOutTimer.schedule(instructionTimeOutTimerTask, instruction.getInstructionTimeout());
        getInstructionStepList(null, true);
        if(instruction.isFinish())
            logicCallbackListener.onInstructionFinish(instruction.getId(), instruction.getInstructionResult());
        else
            sendRequest();
    }

    public boolean isBusy() {
        return busy;
    }

    public void cancelExecution() {
        cancelAllRequests();
        finishWork();
    }

    private void getInstructionStepList(ArrayList<HttpClientResponse> httpClientResponse,
                                        boolean execution){
        httpClientResponses.clear();
        try {
            if (execution)
                instructionStepList = instruction.execute();
            else
                instructionStepList = instruction.getRequestList(httpClientResponse);
            if (instructionStepList.getRequestDescription() != null)
                logicCallbackListener.onStep(
                        instruction.getId(),
                        instructionStepList.getRequestDescription());
            for (HashMap<String, String> map: instructionStepList.getAdditionalCookies()) {
                httpsClient.addCookie(
                        map.get(RequestList.KEY_COOKIE_DOMAIN),
                        map.get(RequestList.KEY_COOKIE_PATH),
                        map.get(RequestList.KEY_COOKIE_NAME),
                        map.get(RequestList.KEY_COOKIE_VALUE),
                        Long.parseLong(
                                Objects.requireNonNull(
                                        map.get(RequestList.KEY_COOKIE_EXPIRES_AT))));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest() {
        if (instructionStepList.size() > 0 && httpsClient.callCount() < maxCountOfRequests) {
            Request request = instructionStepList.getNext();
            HttpClientResponse httpClientResponse = new HttpClientResponse(request);
            if (instructionStepList.isMainInstructionStep(request))
                httpClientResponse.setMainResponse();
            httpsClient.sendRequest(httpClientResponse);
            if (instructionStepList.size() > 1) {
                nextRequestTimer.schedule(
                        nextRequestTimerTask,
                        getRandomDelay(request.getRequestDelay(), request.getRandomRate()));
            } else {
                nextStepTimer.schedule(
                        nextStepTimerTask,
                        getRandomDelay(request.getRequestDelay(), request.getRandomRate()));
            }
            instructionStepList.remove(request);
        }
    }

    @Override
    public void onClientError(HttpClientResponse httpClientResponse) {
        if (addResponseInCaseError(httpClientResponse))
            httpClientResponses.add(httpClientResponse);
        else {
            cancelAllRequests();
            stopAllTimers();
            throw new RuntimeException("onClientError() not supported");
            //TODO сделать обработку возвратного метода onClientError
        }
    }

    @Override
    public void onConnectionFailed(HttpClientResponse httpClientResponse) {
        if (addResponseInCaseError(httpClientResponse))
            httpClientResponses.add(httpClientResponse);
        else {
            finishWork();
            cancelAllRequests();
            logicCallbackListener.onConnectionFailed(instruction.getId(), instruction.getAttempt());
        }
    }

    @Override
    public void onSuccess(HttpClientResponse httpClientResponse) {
        sendRequest();
        httpClientResponses.add(httpClientResponse);
        if (instructionStepList.size() == 0 && httpsClient.callCount() == 0) {
            if (instruction.isFinish()) {
                finishWork();
                logicCallbackListener.onInstructionFinish(
                        instruction.getId(),
                        instruction.getInstructionResult());
            } else {
                getInstructionStepList(httpClientResponses, false);
                if (instruction.isFinish()) {
                    finishWork();
                    logicCallbackListener.onInstructionFinish(
                            instruction.getId(),
                            instruction.getInstructionResult());
                }
                else if (instructionStepList.isUserDataNeeded()) {
                    stopAllTimers();
                    throw new RuntimeException("isUserDataNeeded() not supported");
                }
                else {
                    nextStepTimer.schedule(
                            nextStepTimerTask,
                            getRandomDelay(
                                    instructionStepList.getStepDelay(),
                                    instructionStepList.getRandomRate()));
                }
            }
        }
    }

    private void finishWork() {
        busy = false;
        stopAllTimers();
    }

    private void cancelAllRequests() {
        httpsClient.cancelRequests();
    }

    private void stopAllTimers() {
        instructionTimeOutTimer.cancel();
        nextStepTimer.cancel();
        nextRequestTimer.cancel();
    }

    private long getRandomDelay(long stepDelay, int randomRate) {
        if (stepDelay == 0) {
            stepDelay = defaultStepDelay;
            if (randomRate == 0)
                randomRate = defaultRandomRate;
        }
        if (randomRate == 0)
            return stepDelay;
        long min = stepDelay - (stepDelay * randomRate / 100);
        long max = stepDelay + (stepDelay * randomRate / 100);
        return min + (long) (Math.random() * (max - min));
    }

    private boolean addResponseInCaseError(HttpClientResponse httpClientResponse) {
        return httpClientResponse.getRequest().getPersistence() == Request.STRONG_PERSISTENCE;
    }
}