package org.example.processor.instructions.RInstructions;

import org.example.processor.executionUnits.EU;

public class ADDInstruction extends RInstruction{
    public ADDInstruction(Opcode opcode, int PC, byte rs1, byte rs2, byte rd) {
        super(opcode, PC, rs1, rs2, rd);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
}
