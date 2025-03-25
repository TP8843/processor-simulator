package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.BInstructions.*;
import org.example.processor.instructions.Instruction;

public record CompareUnit(Buffer<Instruction> input, Buffer<Instruction> output) {

    public void execute() {
        // If input is null (processor stalled), do not do any processing
        if (!input.hasValue() || !output.hasSpace()) {
            return;
        }

        Instruction instruction = input.pop().get();
        
        System.out.println("Processing instruction in compare unit: " + instruction);

        switch (instruction) {
            case BEQInstruction i -> i.addResult(i.getRs1Data() == i.getRs2Data() ? 1 : 0);
            case BNEInstruction i -> i.addResult(i.getRs1Data() != i.getRs2Data() ? 1 : 0);
            case BLTInstruction i -> i.addResult(i.getRs1Data() < i.getRs2Data() ? 1 : 0);
            case BGTEInstruction i -> i.addResult(i.getRs1Data() >= i.getRs2Data() ? 1 : 0);
            case BLTUInstruction i -> i.addResult(Integer.compareUnsigned(i.getRs1Data(), i.getRs2Data()) < 0 ? 1 : 0);
            case BGTEUInstruction i -> i.addResult(Integer.compareUnsigned(i.getRs1Data(), i.getRs2Data()) >= 0 ? 1 : 0);
            default -> {
            }
        }

        output.put(instruction);
    }

    @Override
    public String toString() {
        return String.format("""
                Compare Unit - nothing anymore""");
    }
}
