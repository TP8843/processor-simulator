package org.example.processor.instructions.UInstructions;

import org.example.processor.executionUnits.EU;

/// Load Upper Immediate Instruction
public class LUIInstruction extends UInstruction {
    public LUIInstruction(Opcode opcode, int PC, int imm, byte rd) {
        super(opcode, PC, imm, rd);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
}
