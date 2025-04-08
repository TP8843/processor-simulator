package org.example.processor.instructions.IInstructions;

import org.example.processor.executionUnits.EU;

public class LBUInstruction extends IInstruction{
    public LBUInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        super(opcode, PC, rs1, imm, rd);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
}
