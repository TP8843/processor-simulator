package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.InstructionVisitable;
import org.example.processor.instructions.JInstructions.JALInstruction;

import java.util.HashMap;
import java.util.Map;

public class BranchUnit implements InstructionVisitable {
    // TODO: Add properties for all the buffers so they can be flushed on a branch miss
    
    private final InstructionFetch instructionFetch;

    public Buffer<Instruction> compareInput;
    public Buffer<Instruction> decodeInput;
    
    private Map<Integer, Integer> branchAddresses;
    
    private boolean updatedPC;

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
        Instruction decode = decodeInput.pop().get();
        
        decode.visit(this);
        compare.visit(this);
        
        boolean returnValue = updatedPC;
        updatedPC = false;
        return returnValue;
    }
    
    /// Generates address for branch unit
    public void generateAddress(){
        if (decodeInput.hasValue())
            decodeInput.pop().get().visit(this);
    }
    
    public void execute(Instruction instruction){}
    
    /// Address generation for Jump and Link Register Instruction
    public void execute(JALRInstruction instruction){
        updatedPC = true;
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
                instructionFetch.updatePC(instruction.getPC());
        }
    }
    
    /// Address generation for Jump and Link Instruction
    public void execute(JALInstruction instruction) {
        instructionFetch.updatePC(instruction.imm + instruction.getPC());
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit - nothing now :0""", decodeInput, compareInput);
    }
}
