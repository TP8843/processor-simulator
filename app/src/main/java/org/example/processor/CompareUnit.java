package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.Instruction;

public class CompareUnit {
    public final Buffer<Instruction> input;
    public final Buffer<Instruction> output;

    public CompareUnit(Buffer<Instruction> input, Buffer<Instruction> output) {
        this.input = input;
        this.output = output;
    }
    
    public void execute() {
        // If input is null (processor stalled), do not do any processing
        if (!input.hasValue() || !output.hasSpace()) {
            return;
        }
        
        Instruction instruction = input.pop().get();
        
        if (instruction.getType() == Instruction.Type.B_TYPE) {
            output.put(executeBType((BInstruction) instruction));
        } else {
            output.put(instruction);
        }
    }

    static private BInstruction executeBType(BInstruction instruction) {
        boolean result = switch (instruction.type) {
            case BRANCH_EQ -> instruction.rs1Data == instruction.rs2Data;
            case BRANCH_NE -> instruction.rs1Data != instruction.rs2Data;
            case BRANCH_LT -> instruction.rs1Data < instruction.rs2Data;
            case BRANCH_GTE -> instruction.rs1Data >= instruction.rs2Data;

            case BRANCH_LT_UNSIGNED ->
                    Integer.compareUnsigned(instruction.rs1Data, instruction.rs2Data) < 0;
            case BRANCH_GTE_UNSIGNED ->
                    Integer.compareUnsigned(instruction.rs1Data, instruction.rs2Data) >= 0;
        };

        System.out.println("Comparing " + instruction.rs1Data + " and " + instruction.rs2Data + " got " + result);
        return instruction.addCompareResult(result);
    }

    @Override
    public String toString() {
        return String.format("""
                Compare Unit - nothing anymore""");
    }
}
