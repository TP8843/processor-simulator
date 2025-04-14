package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.Flushable;
import org.example.processor.instructions.BInstructions.*;
import org.example.processor.instructions.Branch;
import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstructions.JALInstruction;
import org.example.processor.instructions.UndecodedInstruction;

import java.util.Optional;

public class BranchUnit {
    private final InstructionFetch instructionFetch;
    
    /// Buffers to be flushed for jump/branch instructions
    private final Flushable[] jumpBuffers;

    /// Buffers to be flushed on a mispredict for a branch instruction
    private final Flushable[] mispredictBuffers;

    /// Allow the fetch decode buffer to be released on a branch mispredic
    private final Buffer<UndecodedInstruction> fetchDecodeBuffer;

    public Buffer<Instruction> decodeInput;

    /// The number of times a branch has been issued
    private int branchCount;

    /// The number of branch mispredicts
    private int mispredictCount;

    public BranchUnit(InstructionFetch instructionFetch,
                      Buffer<Instruction> decodeInput,
                      Flushable[] jumpBuffers,
                      Flushable[] mispredictBuffers,
                      Buffer<UndecodedInstruction> fetchDecodeBuffer) {
        this.instructionFetch = instructionFetch;
        this.decodeInput = decodeInput;
        this.jumpBuffers = jumpBuffers;
        this.mispredictBuffers = mispredictBuffers;
        this.fetchDecodeBuffer = fetchDecodeBuffer;
    }

    /// The number of times a branch has been issued
    public int getBranchCount() {
        return branchCount;
    }

    /// The number of branch mispredictions
    public int getMispredictCount() {
        return mispredictCount;
    }

    /// Called if a branch shouldn't have occurred.
    /// Updates the PC and flushes buffers
    public void branchMispredict(Branch instruction) {
        // If we shouldn't have branched, panic (or, update the PC, flush the required buffers, and chill)
        if(instruction.hasResult() && !instruction.getResult()){
            mispredictCount += 1;

            for (Flushable f : mispredictBuffers) f.flush();
            this.fetchDecodeBuffer.release();

            // Update PC to the next instruction after the branch
            instructionFetch.updatePC(instruction.getPC() + 4);
        }
    }
    
    /// Generates address for branch unit. True if memory/write-back should be stalled
    public boolean generateAddress(){
        Optional<Instruction> value = decodeInput.pop();
        if(value.isEmpty()) return false;

        Instruction instruction = value.get();
        
        switch (instruction) {
            case JALRInstruction i -> {
                instructionFetch.updatePC(((i.rs1.getData() + i.imm) >> 1) << 1);
                for (Flushable f : jumpBuffers) f.flush();
                return true;
            }
            
            case JALInstruction i -> {
                instructionFetch.updatePC(i.imm + i.getPC());
                for (Flushable f : jumpBuffers) f.flush();
                return true;
            }

            case BInstruction i -> {
                branchCount += 1;

                instructionFetch.updatePC(i.imm + i.getPC());
                for (Flushable f : jumpBuffers) f.flush();
                return true;
            }
            default -> {}
        }

        return true;
    }
}
