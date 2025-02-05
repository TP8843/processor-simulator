package org.example.processor;

import org.example.processor.instructions.BInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstruction;

public class BranchUnit {
    private final InstructionFetch instructionFetch;

    public Instruction compareInput;
    public Instruction aluInput;

    public BranchUnit(InstructionFetch instructionFetch) {
        this.instructionFetch = instructionFetch;
    }

    public void updatePC() {
        switch (compareInput.getType()){
            case B_TYPE -> updatePCBType(
                    (BInstruction) compareInput,
                    (BInstruction) aluInput);

            case J_TYPE -> updatePCJType((JInstruction) aluInput);
        }
    }

    private void updatePCBType(BInstruction compare, BInstruction alu ) {
        if (compare.compareResult) {
            instructionFetch.updatePC(alu.aluResult);
        }
    }

    private void updatePCJType(JInstruction alu ) {
        instructionFetch.updatePC(alu.aluResult);
    }
}
