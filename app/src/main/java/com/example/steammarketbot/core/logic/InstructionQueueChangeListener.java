package com.example.steammarketbot.core.logic;

import com.example.steammarketbot.core.instructions.Instruction;

public interface InstructionQueueChangeListener {

    void onQueueChanged(Instruction nextInstruction);
    void onRemoveInstructionFromQueue(Instruction removedInstruction);
}
