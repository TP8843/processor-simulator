package org.example.processor.instructions;

public interface Branch extends Instruction{
    /// True if a compare result has been added to the instruction
    boolean hasResult();

    /// Get the result of the comparison for the branch
    boolean getResult();

    @Override
    default boolean isReady(){
        return hasResult();
    }
}
