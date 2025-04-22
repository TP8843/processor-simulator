package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.commit.ROB;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstructions.SInstruction;

import java.util.Optional;

public record Agu(Buffer<Instruction> input, Buffer<LoadInstruction> loadOutput, ROB rob) {
    /// Generates addresses for load and store instructions
    public void execute() {
        Optional<Instruction> value = input.pop();
        if (value.isEmpty()) return;
        Instruction instruction = value.get();

        switch (instruction) {
            case LoadInstruction i -> {
                i.addAddress(i.rs1.getData() + i.imm);
                loadOutput.put(i);
            }
            case SInstruction i -> i.addAddress(i.rs1.getData() + i.imm);

            default -> throw new IllegalArgumentException("Instruction not valid for AGU: " + instruction);
        }
    }
}
