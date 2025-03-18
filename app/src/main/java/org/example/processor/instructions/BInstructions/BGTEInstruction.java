package org.example.processor.instructions.BInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.InstructionVisitable;

public class BGTEInstruction extends BInstruction {
    public BGTEInstruction(Opcode opcode, int PC, byte rs1, byte rs2, int imm) {
        super(opcode, PC, rs1, rs2, imm);
    }

    @Override
    public EU getEU() {
        return EU.COMPARE;
    }
    
    @Override
    public void visit(InstructionVisitable visitable) {
        visitable.execute(this);
    }
}
