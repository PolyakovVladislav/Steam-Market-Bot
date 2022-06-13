package com.example.steammarketbot.core;

import com.example.steammarketbot.core.instructions.InstructionResult;

public interface LogicCallbackListener {

    void onInstructionTimeout(int id, int attempt);
    void onConnectionFailed(int id, int attempt);
    void onInstructionFinish(int id, InstructionResult instructionResult);
    void onUserDataNeeded(int id, int code);
    void onStep(int id, String stepDescription);
}
