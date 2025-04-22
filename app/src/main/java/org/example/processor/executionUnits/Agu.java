package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.ManualReleaseReservationStation;
import org.example.processor.commit.ROB;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstructions.SInstruction;

import java.util.Optional;

public record Agu(ManualReleaseReservationStation input, Buffer<LoadInstruction> loadOutput, ROB rob) {
    /// Generates addresses for load and store instructions
    public void execute() {

        Optional<Instruction> value = input.peek();
        if (value.isEmpty()) return;
        Instruction instruction = value.get();

        switch (instruction) {
            case LoadInstruction i -> {
                if(!loadOutput.hasSpace()) return;
                input.pop();
                i.addAddress(i.rs1.getData() + i.imm);
                loadOutput.put(i);
            }
            case SInstruction i -> {
                input.pop();
                i.addAddress(i.rs1.getData() + i.imm);
                input.release();
            }

            default -> throw new IllegalArgumentException("Instruction not valid for AGU: " + instruction);
        }
    }
}
