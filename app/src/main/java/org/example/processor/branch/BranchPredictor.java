package org.example.processor.branch;

import org.example.processor.instructions.BInstructions.BInstruction;

public interface BranchPredictor {
    /// Predict whether to branch or not
    boolean predict(BInstruction instruction);
}
