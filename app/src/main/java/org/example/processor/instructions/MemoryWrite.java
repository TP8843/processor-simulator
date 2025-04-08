package org.example.processor.instructions;

public interface MemoryWrite extends Instruction {
    /// Address for write has been added to instruction
    boolean hasAddress();

    /// Get address to write value to
    int getAddress();

    /// Get value to write to memory
    int getValue();

    @Override
    default boolean isReady() {
        return hasAddress();
    }
}
