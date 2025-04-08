package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.Flushable;
import org.example.processor.buffers.SingleValueBuffer;
import org.example.processor.instructions.BInstructions.*;
import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstructions.JALInstruction;

public class BranchUnit {
    private final InstructionFetch instructionFetch;
    
    /// Buffers to be flushed for a jump instruction
    private final Flushable[] jumpBuffers;
    
    /// Buffers to be flushed for a branch instructon
    private final Flushable[] branchBuffers;

    public Buffer<Instruction> decodeInput;

    /// Holds instruction between address generation and comparison stages
    private final Buffer<Instruction> internal = new SingleValueBuffer<>();

    /// Stores the final address for the current branch instruction
    private int address;

    public BranchUnit(InstructionFetch instructionFetch,
                      Buffer<Instruction> decodeInput,
                      Flushable[] jumpBuffers,
                      Flushable[] branchBuffers) {
        this.instructionFetch = instructionFetch;
        this.decodeInput = decodeInput;
//        this.branchAddresses = new HashMap<>();
        this.jumpBuffers = jumpBuffers;
        this.branchBuffers = branchBuffers;
    }

    /// Updates the PC in instruction fetch if required. Returns true if branch processed / can release memory buffer
    public boolean updatePC() {
        // Do not do any processing if either input is null (something has stalled)
        if(!internal.hasValue()) return false;
        
        Instruction instruction = internal.pop().get();

        switch (instruction) {
            case BEQInstruction i -> i.addCompareResult(i.getRs1Data() == i.getRs2Data());
            case BNEInstruction i -> i.addCompareResult(i.getRs1Data() != i.getRs2Data());
            case BLTInstruction i -> i.addCompareResult(i.getRs1Data() < i.getRs2Data());
            case BGTEInstruction i -> i.addCompareResult(i.getRs1Data() >= i.getRs2Data());
            case BLTUInstruction i -> i.addCompareResult(Integer.compareUnsigned(i.getRs1Data(), i.getRs2Data()) < 0);
            case BGTEUInstruction i -> i.addCompareResult(Integer.compareUnsigned(i.getRs1Data(), i.getRs2Data()) >= 0);
            default -> {
            }
        }

        if (instruction instanceof BInstruction i) {
            if (i.hasCompareResult() && i.getCompareResult()) {
                System.out.println("I'm about to branch out " + i);
                instructionFetch.updatePC(address);
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
                System.out.println("JALR instruction" + i);
                return true;
            }
            
            case JALInstruction i -> {
                instructionFetch.updatePC(i.imm + i.getPC());
                for (Flushable f : jumpBuffers) f.flush();
                System.out.println("JAL instruction" + i);
                return true;
            }

            case BInstruction i -> {
                address = i.getPC() + i.imm;
                internal.put(i);
                return false;
            }
            default -> {}
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("""
                Branch Unit -
                  Address: %s
                  Internal Buffer: %s""", this.address, this.internal);
    }
}
