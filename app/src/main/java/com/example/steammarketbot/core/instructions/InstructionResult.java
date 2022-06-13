package com.example.steammarketbot.core.instructions;

import com.example.steammarketbot.core.profileInfo.Wallet;
import com.example.steammarketbot.core.profileInfo.lot.LotForBuyList;
import com.example.steammarketbot.core.profileInfo.lot.LotForSellList;

public class InstructionResult {

    public final static int CODE_TWO_FACTOR_AUTH_INCORRECT = 1;
    public final static int SESSION_ID_NOT_FOUND = 1;

    private boolean success;
    private String message;
    private int code;
    private String sessionId;
    private Wallet wallet;
    private LotForBuyList lotForBuyList;
    private LotForSellList sellLotForSellList;

    public InstructionResult() {
        success = false;
        message = "";
        code = 0;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setSuccess() {
        success = true;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public LotForBuyList getBuyLotList() {
        return lotForBuyList;
    }

    public void setBuyLotList(LotForBuyList lotForBuyList) {
        this.lotForBuyList = this.lotForBuyList;
    }

    public LotForSellList getSellLotList() {
        return sellLotForSellList;
    }

    public void setSellLotList(LotForSellList sellLotForSellList) {
        this.sellLotForSellList = sellLotForSellList;
    }
}
