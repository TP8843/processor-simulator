package org.example.processor.instructions.UInstructions;

import org.example.processor.executionUnits.EU;

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
