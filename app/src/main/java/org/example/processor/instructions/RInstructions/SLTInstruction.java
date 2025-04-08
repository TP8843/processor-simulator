package org.example.processor.instructions.RInstructions;

import org.example.processor.executionUnits.EU;

/// Set Less Than Instruction
public class SLTInstruction extends RInstruction{
    public SLTInstruction(Opcode opcode, int PC, byte rs1, byte rs2, byte rd) {
        super(opcode, PC, rs1, rs2, rd);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
}
