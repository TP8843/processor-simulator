package org.example.processor.instructions;

public interface Branch {
    /// True if a compare result has been added to the instruction
    boolean hasCompareResult();

    /// Get the result of the comparison for the branch
    public boolean getCompareResult();
}
