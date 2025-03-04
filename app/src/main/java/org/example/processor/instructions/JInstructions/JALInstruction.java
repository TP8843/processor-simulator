package org.example.processor.instructions.JInstructions;

import org.example.processor.instructions.InstructionVisitable;

public class JALInstruction extends JInstruction {
    public JALInstruction(Opcode opcode, int PC, int imm, byte rd) {
        super(opcode, PC, imm, rd);
    }

    @Override
    public void visit(InstructionVisitable visitable) {
        visitable.execute(this);
    }
}
