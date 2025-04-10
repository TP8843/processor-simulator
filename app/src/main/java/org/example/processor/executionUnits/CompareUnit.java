package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.BInstructions.*;
import org.example.processor.instructions.Instruction;

public class CompareUnit {
    /// Buffer to receive instructions from the reservation station
    public Buffer<Instruction> input;

    public CompareUnit(Buffer<Instruction> input) {
        this.input = input;
    }

    /// Runs execution for an instruction, and adds the comparison result
    public void execute() {
        // If input not available, do not run anything
        if (!input.hasValue()) return;

        Instruction instruction = input.pop().get();

        switch (instruction) {
            case BEQInstruction i -> i.addResult(i.rs1.getData() == i.rs2.getData());
            case BNEInstruction i -> i.addResult(i.rs1.getData() != i.rs2.getData());
            case BLTInstruction i -> i.addResult(i.rs1.getData() < i.rs2.getData());
            case BGTEInstruction i -> i.addResult(i.rs1.getData() >= i.rs2.getData());
            case BLTUInstruction i -> i.addResult(Integer.compareUnsigned(i.rs1.getData(), i.rs2.getData()) < 0);
            case BGTEUInstruction i -> i.addResult(Integer.compareUnsigned(i.rs1.getData(), i.rs2.getData()) >= 0);
            default -> {
            }
        }
    }
}
