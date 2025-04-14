package org.example.processor.instructions.RInstructions.multiply;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.RInstructions.RInstruction;

public class MInstruction extends RInstruction {
    public MInstruction(Opcode opcode, int PC, byte rs1, byte rs2, byte rd) {
        super(opcode, PC, rs1, rs2, rd);
    }

    @Override
    public EU getEU() {
        return EU.MULTIPLY;
    }
}
