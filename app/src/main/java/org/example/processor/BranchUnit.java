package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.BInstruction;
import org.example.processor.instructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstruction;

// TODO: Make calculation for relative branches happen somewhere earlier in the pipeline (allow branch prediction)

public class BranchUnit {
    private final InstructionFetch instructionFetch;

    public Buffer<Instruction> compareInput;
    public Buffer<Instruction> aluInput;

    public BranchUnit(InstructionFetch instructionFetch, Buffer<Instruction> compareInput, Buffer<Instruction> aluInput) {
        this.instructionFetch = instructionFetch;
        this.compareInput = compareInput;
        this.aluInput = aluInput;
    }

    /// Updates the PC in instruction fetch if required. Returns true if PC updated
    public boolean updatePC() {
        // Do not do any processing if either input is null (something has stalled)
        if(!compareInput.hasValue() || aluInput.hasValue()) return false;
        
        Instruction compare = compareInput.pop().get();
        Instruction alu = aluInput.pop().get();
        
        return switch (compare.getType()){
            case B_TYPE -> updatePCBType(
                    (BInstruction) compare,
                    (BInstruction) alu);

            case J_TYPE -> updatePCJType((JInstruction) alu);
            case I_TYPE -> updatePCIType((IInstruction) alu);
            default -> false;
        };
    }

    private boolean updatePCBType(BInstruction compare, BInstruction alu ) {
        if (compare.compareResult) {
            instructionFetch.updatePC(alu.aluResult);
            return true;
        }
        return false;
    }

    private boolean updatePCJType(JInstruction alu ) {
        instructionFetch.updatePC(alu.aluResult);
        return true;
    }

    private boolean updatePCIType(IInstruction alu ) {
        if (alu.type == IInstruction.Type.JUMP_AND_LINK_REGISTER) {
            instructionFetch.updatePC(alu.aluResult);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit - nothing now :0""", aluInput, compareInput);
    }
}
