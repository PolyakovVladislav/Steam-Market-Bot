package com.example.steammarketbot.core.logic;

import com.example.steammarketbot.core.instructions.Instruction;

import java.util.ArrayList;
import java.util.Comparator;

public class InstructionQueue {
    
    ArrayList<Instruction> instructions;
    InstructionQueueChangeListener instructionQueueChangeListener;

    public InstructionQueue(InstructionQueueChangeListener instructionQueueChangeListener) {
        this.instructionQueueChangeListener = instructionQueueChangeListener;
    }

    public void addInstructions(ArrayList<Instruction> instructions) {
        this.instructions.addAll(instructions);
        sortInstructions();
        instructionQueueChangeListener.onQueueChanged(instructions.get(0));
    }

    public void addInstruction(Instruction instruction) {
        instructions.add(instruction);
        sortInstructions();
        instructionQueueChangeListener.onQueueChanged(instructions.get(0));
    }

    public Instruction getNextInstruction() {
        return instructions.get(0);
    }

    public Instruction getInstructionById(int id) {
        for (Instruction instruction: instructions) {
            if (instruction.getId() == id)
                return instruction;
        }
        return null;
    }

    public void removeInstructionById(int id) {
        for (Instruction instruction: instructions) {
            if (instruction.getId() == id) {
                instructions.remove(instruction);
                instructionQueueChangeListener.onRemoveInstructionFromQueue(instruction);
                instructionQueueChangeListener.onQueueChanged(getNextInstruction());
                return;
            }
        }
    }

    public void clear() {
        instructions.clear();
    }

    public int size() {
        return instructions.size();
    }

    private void sortInstructions() {
        instructions.sort(new Comparator<Instruction>() {
            @Override
            public int compare(Instruction i1, Instruction i2) {
                int i;
                i = Math.toIntExact(i2.getTimeWindowStarts() - i1.getTimeWindowStarts());
                if (i == 0)
                    i = i2.getPriority() - i1.getPriority();
                return i;
            }
        });
    }
}
