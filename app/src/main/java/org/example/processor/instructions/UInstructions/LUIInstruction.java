package org.example.processor.instructions.UInstructions;

import org.example.processor.instructions.InstructionVisitable;

/// Load Upper Immediate Instruction
public class LUIInstruction extends UInstruction {
    public LUIInstruction(Opcode opcode, int PC, int imm, byte rd) {
        super(opcode, PC, imm, rd);
    }

    @Override
    public void visit(InstructionVisitable visitable) {
        visitable.execute(this);
    }
}
