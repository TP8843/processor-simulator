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
    
    /// Whether end of the program has been reached (j 0)
    private boolean endReached = false;
    
    private Map<Integer, Integer> branchAddresses;

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
    
    /// Whether the end of the program has been reached
    public boolean getEndReached() {
        return endReached;
    }

    /// Updates the PC in instruction fetch if required. Returns true if PC updated
    public void updatePC() {
        // Do not do any processing if either input is null (something has stalled)
        if(!compareInput.hasValue() || !decodeInput.hasValue()) return;
        
        Instruction instruction = compareInput.pop().get();

        if (instruction instanceof BInstruction i) {
            if (i.getResult() != 0) {
                instructionFetch.updatePC(branchAddresses.remove(instruction.getPC()));
                for (Flushable f : branchBuffers) f.flush();
            }
        }
    }
    
    /// Generates address for branch unit
    public void generateAddress(){
        if (!decodeInput.hasValue()) return;
        
        Instruction instruction = decodeInput.pop().get();
        
        switch (instruction) {
            case JALRInstruction i -> {
                instructionFetch.updatePC(((i.rs1Data + i.imm) >> 1) << 1);
                for (Flushable f : jumpBuffers) f.flush();
            }
            
            case JALInstruction i -> {
                // Check if instruction is a stall instruction
                if(i.imm + i.getPC() == 0)
                    endReached = true;
                
                instructionFetch.updatePC(i.imm + i.getPC());
                for (Flushable f : jumpBuffers) f.flush();
            }
            case BInstruction i -> branchAddresses.put(i.getPC(), i.getPC() + i.imm);
            default -> {}
        }
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit - Branch Addresses:
                %s""", this.branchAddresses);
    }
}
