package org.example.processor.branch.strategies;

import org.example.processor.instructions.BInstructions.BInstruction;

public class BranchBackwards implements BranchStrategy {
    @Override
    public boolean predict(BInstruction instruction) {
        return instruction.imm < 0;
    }
}
