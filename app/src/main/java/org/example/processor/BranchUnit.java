package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.Flushable;
import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstructions.JALInstruction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchUnit {
    private final InstructionFetch instructionFetch;
    
    /// Buffers to be flushed for a jump instruction
    private final Flushable[] jumpBuffers;
    
    /// Buffers to be flushed for a branch instructon
    private final Flushable[] branchBuffers;

    public Buffer<Instruction> compareInput;
    public Buffer<Instruction> decodeInput;
    
    private final Map<Integer, Integer> branchAddresses;

    public BranchUnit(InstructionFetch instructionFetch, 
                      Buffer<Instruction> compareInput, 
                      Buffer<Instruction> decodeInput,
                      Flushable[] jumpBuffers,
                      Flushable[] branchBuffers) {
        this.instructionFetch = instructionFetch;
        this.compareInput = compareInput;
        this.decodeInput = decodeInput;
        this.branchAddresses = new HashMap<>();
        this.jumpBuffers = jumpBuffers;
        this.branchBuffers = branchBuffers;
    }

    /// Updates the PC in instruction fetch if required. Returns true if branch processed / can release memory buffer
    public boolean updatePC() {
        // Do not do any processing if either input is null (something has stalled)
        if(!compareInput.hasValue()) return false;
        
        Instruction instruction = compareInput.pop().get();

        if (instruction instanceof BInstruction i) {
            if (i.getResult() != 0) {
                instructionFetch.updatePC(branchAddresses.remove(instruction.getPC()));
                for (Flushable f : branchBuffers) f.flush();
            }

            return true;
        }

        return false;
    }
    
    /// Generates address for branch unit. True if memory/write-back should be stalled
    public boolean generateAddress(){
        if (!decodeInput.hasValue()) return false;
        
        Instruction instruction = decodeInput.pop().get();
        
        switch (instruction) {
            case JALRInstruction i -> {
                instructionFetch.updatePC(((i.rs1Data + i.imm) >> 1) << 1);
                for (Flushable f : jumpBuffers) f.flush();
            }
            
            case JALInstruction i -> {
                instructionFetch.updatePC(i.imm + i.getPC());
                for (Flushable f : jumpBuffers) f.flush();
            }

            case BInstruction i -> {
                branchAddresses.put(i.getPC(), i.getPC() + i.imm);
                return true;
            }
            default -> {}
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit - Branch Addresses:
                %s""", this.branchAddresses);
    }
}
