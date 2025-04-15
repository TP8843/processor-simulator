package org.example.processor.branch.strategies;

import org.example.processor.instructions.BInstructions.BInstruction;

public class NeverBranch implements BranchStrategy {
    @Override
    public boolean predict(BInstruction instruction) {
        return false;
    }
}
