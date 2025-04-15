package org.example.processor.instructions;

public interface Branch extends Instruction{
    /// True if a compare result has been added to the instruction
    boolean hasResult();

    /// Get the result of the comparison for the branch
    boolean getResult();

    /// Get whether the instruction was speculatively carried out earlier
    boolean getSpeculativeBranch();

    /// Get the address of the branch
    int getAddress();

    @Override
    default boolean isReady(){
        return hasResult();
    }
}
