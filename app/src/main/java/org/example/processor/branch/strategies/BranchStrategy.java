package org.example.processor.branch.strategies;

import org.example.processor.instructions.BInstructions.BInstruction;

public interface BranchStrategy {
    /// Predict whether to branch or not
    boolean predict(BInstruction instruction);
}
