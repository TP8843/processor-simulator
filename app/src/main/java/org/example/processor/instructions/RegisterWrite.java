package org.example.processor.instructions;

public interface RegisterWrite {
    /// Result has been added to instruction
    boolean hasResult();

    /// Get result for instruction
    int getResult();
}
