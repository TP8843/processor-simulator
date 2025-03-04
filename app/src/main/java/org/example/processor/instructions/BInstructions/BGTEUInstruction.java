package org.example.processor.instructions.BInstructions;

import org.example.processor.instructions.InstructionVisitable;

public class BGTEUInstruction extends BInstruction {
    public BGTEUInstruction(Opcode opcode, int PC, byte rs1, byte rs2, int imm) {
        super(opcode, PC, rs1, rs2, imm);
    }

    @Override
    public void visit(InstructionVisitable visitable) {
        visitable.execute(this);
    }
}
