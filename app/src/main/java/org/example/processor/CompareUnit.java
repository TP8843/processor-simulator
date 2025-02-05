package org.example.processor;

import org.example.processor.instructions.BInstruction;
import org.example.processor.instructions.Instruction;

public class CompareUnit {
    public Instruction input;
    public Instruction output;

    public void execute() {
        if (input.getType() == Instruction.Type.B_TYPE) {
            output = executeBType((BInstruction) input);
        } else {
            output = input;
        }
    }

    static private BInstruction executeBType(BInstruction instruction) {
        boolean result = switch (instruction.type) {
            case BRANCH_EQ -> instruction.rs1 == instruction.rs2;
            case BRANCH_NE -> instruction.rs1 != instruction.rs2;
            case BRANCH_LT -> instruction.rs1 < instruction.rs2;
            case BRANCH_GTE -> instruction.rs1 >= instruction.rs2;

            case BRANCH_LT_UNSIGNED ->
                    Integer.compareUnsigned(instruction.rs1, instruction.rs2) < 0;
            case BRANCH_GTE_UNSIGNED ->
                    Integer.compareUnsigned(instruction.rs1, instruction.rs2) >= 0;
        };

        return instruction.addCompareResult(result);
    }
}
