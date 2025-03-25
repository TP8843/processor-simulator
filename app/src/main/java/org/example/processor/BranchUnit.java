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
    // TODO: Add properties for all the buffers so they can be flushed on a branch miss
    
    private final InstructionFetch instructionFetch;
    
    /// Buffers to be flushed for a jump instruction
    private final List<Flushable> jumpBuffers;
    
    /// Buffers to be flushed for a branch instructon
    private final List<Flushable> branchBuffers;

    public Buffer<Instruction> compareInput;
    public Buffer<Instruction> decodeInput;
    
    private Map<Integer, Integer> branchAddresses;

    public BranchUnit(InstructionFetch instructionFetch, 
                      Buffer<Instruction> compareInput, 
                      Buffer<Instruction> decodeInput,
                      List<Flushable> jumpBuffers,
                      List<Flushable> branchBuffers) {
        this.instructionFetch = instructionFetch;
        this.compareInput = compareInput;
        this.decodeInput = decodeInput;
        this.branchAddresses = new HashMap<>();
        this.jumpBuffers = jumpBuffers;
        this.branchBuffers = branchBuffers;
    }

    /// Updates the PC in instruction fetch if required. Returns true if PC updated
    public void updatePC() {
        // Do not do any processing if either input is null (something has stalled)
        if(!compareInput.hasValue() || !decodeInput.hasValue()) return;
        
        Instruction instruction = compareInput.pop().get();
        
        switch (instruction) {
            case BInstruction i -> {
                if (i.getResult() != 0) {
                    instructionFetch.updatePC(branchAddresses.remove(instruction.getPC()));
                    branchBuffers.forEach(buffer -> buffer.flush());
                }
            }
            
            default -> {}
        }
    }
    
    /// Generates address for branch unit
    public void generateAddress(){
        if (!decodeInput.hasValue()) return;
        
        Instruction instruction = decodeInput.pop().get();
        
        switch (instruction) {
            case JALRInstruction i -> {
                instructionFetch.updatePC(((i.rs1Data + i.imm) >> 1) << 1);
                jumpBuffers.forEach(buffer -> buffer.flush());
            }
            
            case JALInstruction i -> {
                instructionFetch.updatePC(i.imm + i.getPC());
                jumpBuffers.forEach(buffer -> buffer.flush());
            }
            case BInstruction i -> branchAddresses.put(i.getPC(), i.getPC() + i.imm);
            default -> {}
        }
    }
    
    public void execute(Instruction instruction){}
    
    /// Address generation for Jump and Link Register Instruction
    public void execute(JALRInstruction instruction){
        instructionFetch.updatePC(((instruction.rs1Data + instruction.imm) >> 1) << 1);
    }

    /// Address generation for Branch Instructions
    public void execute(BInstruction instruction) {
        if(!branchAddresses.containsKey(instruction.getPC())) 
            branchAddresses.put(instruction.getPC(), instruction.getPC() + instruction.imm);
        else {
            // Only update PC if compare is true
            if (instruction.getResult() != 0)
                // TODO: Flush pipeline of incorrect instructions
                instructionFetch.updatePC(branchAddresses.get(instruction.getPC()));
        }
    }
    
    /// Address generation for Jump and Link Instruction
    public void execute(JALInstruction instruction) {
        instructionFetch.updatePC(instruction.imm + instruction.getPC());
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit - Branch Addresses:
                %s""", this.branchAddresses);
    }
}
