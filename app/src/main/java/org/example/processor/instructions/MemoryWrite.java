package org.example.processor.instructions;

public interface MemoryWrite {
    /// Address for write has been added to instruction
    boolean hasAddress();

    /// Get address to write value to
    int getAddress();

    /// Get value to write to memory
    int getValue();
}
