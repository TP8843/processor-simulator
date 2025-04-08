package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstructions.SInstruction;

public record Agu(Buffer<Instruction> input, Buffer<Instruction> loadOutput) {
    /// Generates addresses for load and store instructions
    public void execute() {
        // If input not available or output full, do not run anything
        if (!input.hasValue() || !loadOutput.hasSpace()) {
            return;
        }

        Instruction instruction = input.pop().get();

        switch (instruction) {
            case LoadInstruction i -> i.addAddress(i.rs1Data + i.imm);
            case SInstruction i -> i.addAddress(i.getRs1Data() + i.imm);

            default -> throw new IllegalArgumentException("Instruction not valid for AGU: " + instruction);
        }

        loadOutput.put(instruction);

    }
}
