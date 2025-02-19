package org.example.processor;

import org.example.processor.instructions.BInstruction;
import org.example.processor.instructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstruction;

// TODO: Make calculation for relative branches happen somewhere earlier in the pipeline (allow branch prediction)

public class BranchUnit {
    private final InstructionFetch instructionFetch;

    public Instruction compareInput;
    public Instruction aluInput;

    public BranchUnit(InstructionFetch instructionFetch) {
        this.instructionFetch = instructionFetch;
    }

    public void updatePC() {
        // Do not do any processing if either input is null (something has stalled)
        if(compareInput == null || aluInput == null) return;
        
        switch (compareInput.getType()){
            case B_TYPE -> updatePCBType(
                    (BInstruction) compareInput,
                    (BInstruction) aluInput);

            case J_TYPE -> updatePCJType((JInstruction) aluInput);
            case I_TYPE -> updatePCIType((IInstruction) aluInput);
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

    private void updatePCIType(IInstruction alu ) {
        if (alu.type == IInstruction.Type.JUMP_AND_LINK_REGISTER)
            instructionFetch.updatePC(alu.aluResult);
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit:
                    ALU Input: %s
                    Compare Input: %s""", aluInput, compareInput);
    }
}
