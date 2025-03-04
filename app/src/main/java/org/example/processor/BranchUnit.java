package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.InstructionVisitable;
import org.example.processor.instructions.JInstructions.JInstruction;

import java.util.HashMap;
import java.util.Map;

// TODO: Make calculation for relative branches happen somewhere earlier in the pipeline (allow branch prediction)

public class BranchUnit implements InstructionVisitable {
    private final InstructionFetch instructionFetch;

    public Buffer<Instruction> compareInput;
    public Buffer<Instruction> decodeInput;
    
    private Map<Integer, Integer> branchAddresses;

    public BranchUnit(InstructionFetch instructionFetch, Buffer<Instruction> compareInput, Buffer<Instruction> decodeInput) {
        this.instructionFetch = instructionFetch;
        this.compareInput = compareInput;
        this.decodeInput = decodeInput;
        this.branchAddresses = new HashMap<>();
    }

    /// Updates the PC in instruction fetch if required. Returns true if PC updated
    public boolean updatePC() {
        // Do not do any processing if either input is null (something has stalled)
        if(!compareInput.hasValue() || !decodeInput.hasValue()) return false;
        
        Instruction compare = compareInput.pop().get();
        Instruction alu = decodeInput.pop().get();
        
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
            instructionFetch.updatePC(alu.result);
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
            instructionFetch.updatePC(alu.result);
            return true;
        }
        return false;
    }
    
    /// Generates address for branch unit
    public void generateAddress(){
        if (decodeInput.hasValue())
            decodeInput.pop().get().visit(this);
    }
    
    public void execute(Instruction instruction){}

    public void execute(BInstruction instruction) {
        branchAddresses.put(instruction.getPC(), instruction.getPC() + instruction.imm);
    }

    public void execute(IInstruction instruction) {
        if (instruction.type == IInstruction.Type.JUMP_AND_LINK_REGISTER) {
            branchAddresses.put(instruction.getPC(), ((instruction.rs1Data + instruction.imm) >> 1) << 1);
        }
    }
    
    public void execute(JInstruction instruction) {
        if (instruction.type == JInstruction.Type.JUMP_AND_LINK) {
            branchAddresses.put(instruction.getPC(), instruction.imm + instruction.getPC());
        }
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit - nothing now :0""", decodeInput, compareInput);
    }
}
