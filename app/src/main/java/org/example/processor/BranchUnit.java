package org.example.processor;

import org.example.processor.instructions.BInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstruction;

public class BranchUnit {
    private final InstructionFetch instructionFetch;

    public Instruction input;

    public BranchUnit(InstructionFetch instructionFetch) {
        this.instructionFetch = instructionFetch;
    }

    public void updatePC() {
        switch (input.getType()){
            case B_TYPE -> updatePCBType((BInstruction) input);
            case J_TYPE -> updatePCJType((JInstruction) input);
        }
    }

    private void updatePCBType(BInstruction instruction) {
        if (instruction.compareResult) {
            instructionFetch.updatePC(instruction.aluResult);
        }
    }

    private void updatePCJType(JInstruction instruction) {
        instructionFetch.updatePC(instruction.aluResult);
    }
}
