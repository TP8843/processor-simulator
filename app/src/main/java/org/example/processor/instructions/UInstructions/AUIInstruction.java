package org.example.processor.instructions.UInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.InstructionVisitable;

/// Add Upper Immediate to PC Instruction
public class AUIInstruction extends UInstruction {
    public AUIInstruction(Opcode opcode, int PC, int imm, byte rd) {
        super(opcode, PC, imm, rd);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
}
