package org.example.processor.branch.strategies;

import org.example.processor.instructions.BInstructions.BInstruction;

public class AlwaysBranch implements BranchStrategy {
    @Override
    public boolean predict(BInstruction instruction) {
        return true;
    }
}
