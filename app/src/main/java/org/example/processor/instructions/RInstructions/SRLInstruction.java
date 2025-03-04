package org.example.processor.instructions.RInstructions;

import org.example.processor.instructions.InstructionVisitable;

public class SRLInstruction extends RInstruction{
    public SRLInstruction(Opcode opcode, int PC, byte rs1, byte rs2, byte rd) {
        super(opcode, PC, rs1, rs2, rd);
    }

    @Override
    public void visit(InstructionVisitable visitable) {
        visitable.execute(this);
    }
}
