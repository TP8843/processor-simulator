package org.example.processor.instructions.RInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.InstructionVisitable;

public class ANDInstruction extends RInstruction{
    public ANDInstruction(Opcode opcode, int PC, byte rs1, byte rs2, byte rd) {
        super(opcode, PC, rs1, rs2, rd);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
}
