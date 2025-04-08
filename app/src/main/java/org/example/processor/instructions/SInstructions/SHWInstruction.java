package org.example.processor.instructions.SInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.InstructionVisitable;

/// Store Half Word Instruction
public class SHWInstruction extends SInstruction {
    public SHWInstruction(Opcode opcode, int PC, byte rs1, byte rs2, int imm) {
        super(opcode, PC, rs1, rs2, imm);
    }

    @Override
    public EU getEU() {
        return EU.ALU;
    }
    
    @Override
    public void visit(InstructionVisitable visitable) {
        visitable.execute(this);
    }
}
