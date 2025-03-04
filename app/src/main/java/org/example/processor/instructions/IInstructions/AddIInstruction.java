package org.example.processor.instructions.IInstructions;

import org.example.processor.instructions.InstructionVisitable;

public class AddIInstruction extends IInstruction{
    public AddIInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        super(opcode, PC, rs1, imm, rd);
    }

    @Override
    public void visit(InstructionVisitable visitable) {
        visitable.execute(this);
    }
}
