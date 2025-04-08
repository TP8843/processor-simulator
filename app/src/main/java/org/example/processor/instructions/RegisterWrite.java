package org.example.processor.instructions;

public interface RegisterWrite extends Instruction {
    /// Result has been added to instruction
    boolean hasResult();

    /// Get result for instruction
    int getResult();

    /// Get the register to store the result
    byte getDestination();

    @Override
    default boolean isReady() {
        return hasResult();
    }
}
