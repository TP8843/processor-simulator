package org.example.processor.buffers;

import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.Instruction;

import java.util.Optional;

/// Blocks until the data the instruction requires is available
public class BranchBuffer implements Buffer<Instruction>, Flushable {
    private boolean stalled;
    
    private Instruction value;

    @Override
    public Optional<Instruction> pop() {
        if (value != null) {
            // Only stall if JALR instruction (needs register value for address) and does not yet have data
            if(value instanceof JALRInstruction jalrInstruction){
                if(!jalrInstruction.hasData()) return Optional.empty();
            }

            Instruction value = this.value;
            this.value = null;
            return Optional.of(value);
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<Instruction> peek() {
        if (value != null) {
            return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public boolean hasSpace() {
        return value == null;
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    @Override
    public void stall() {
        stalled = true;
    }

    @Override
    public void release() {
        stalled = false;
    }

    @Override
    public boolean put(Instruction value) {
        if (this.value == null) {
            this.value = value;
            return true;
        }
        
        return false;
    }

    @Override
    public void flush() {
        value = null;
    }
    
    /// Adds the data to the currently stored instruction
    public void addData() {
        // Can only add data if instruction actually in buffer
        if(value == null) return;
        
        value.getDataIfAvailable();
    }

    @Override
    public String toString() {
        return String.format("""
                Current Value: %s
                Stalled: %s""", value, stalled ? "True" : "False");
    }
}
