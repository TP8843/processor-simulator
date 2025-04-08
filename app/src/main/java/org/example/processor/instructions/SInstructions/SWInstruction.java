package org.example.processor.instructions.SInstructions;

import org.example.processor.executionUnits.EU;

public class SWInstruction extends SInstruction {
    public SWInstruction(Opcode opcode, int PC, byte rs1, byte rs2, int imm) {
        super(opcode, PC, rs1, rs2, imm);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
}
