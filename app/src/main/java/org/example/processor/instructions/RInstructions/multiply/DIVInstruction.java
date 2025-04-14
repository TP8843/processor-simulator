package org.example.processor.instructions.RInstructions.multiply;

public class DIVInstruction extends MInstruction {
    public DIVInstruction(Opcode opcode, int PC, byte rs1, byte rs2, byte rd) {
        super(opcode, PC, rs1, rs2, rd);
    }
}
