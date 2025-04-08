package org.example.processor.instructions.IInstructions.LoadInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.IInstructions.IInstruction;

public class LHWInstruction extends LoadInstruction {
    public LHWInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        super(opcode, PC, rs1, imm, rd);
    }
}
