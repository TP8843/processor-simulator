package org.example.processor.branch.strategies;

import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.Branch;

public interface BranchStrategy {
    /// Predict whether to branch or not
    boolean predict(BInstruction instruction);

    /// Update predictor based on completed instruction
    default void update(Branch instruction, boolean branched) {}
}
