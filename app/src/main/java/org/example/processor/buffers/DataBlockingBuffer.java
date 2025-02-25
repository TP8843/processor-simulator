package org.example.processor.buffers;

import org.example.processor.Registers;
import org.example.processor.instructions.Instruction;

import java.util.Optional;

/// Blocks until the data the instruction requires is available
public class DataBlockingBuffer implements Buffer<Instruction> {
    private boolean stalled;
    
    private Instruction value;

    @Override
    public Optional<Instruction> pop() {
        if (value != null && value.hasData()) {
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
        return (stalled == false && value != null && value.hasData());
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
    public void addData(Registers registers) {
        // Can only add data if instruction actually in buffer
        if(value == null) return;
        
        value = value.addDataIfAvailable(registers);
    }

    @Override
    public String toString() {
        return String.format("""
                Current Value: %s""", value);
    }
}
