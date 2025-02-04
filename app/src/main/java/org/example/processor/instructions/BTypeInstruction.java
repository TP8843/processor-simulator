package org.example.processor.instructions;

public class BTypeInstruction implements Instruction {
    private final Instruction.Opcode opcode;

    private final int PC;

    // TODO: Add funct

    /// Data from first source register for instruction
    public final int rs1;

    /// Data from second source register for instruction
    public final int rs2;

    /// Immediate value for instruction
    public final int imm;

    /// Result of processing 
    public final int aluResult;

    public BTypeInstruction(Instruction.Opcode opcode, int PC, int rs1, int rs2, int imm) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
        this.aluResult = 0;
    }

    public BTypeInstruction(Instruction.Opcode opcode, int PC, int rs1, int rs2, int imm, int aluResult) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
        this.aluResult = aluResult;
    }

    @Override
    public Instruction.Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }

    static public int decodeImmediate(int instruction){
        return (((instruction >> 7) & 0b1) << 11) |
                (((instruction >> 8) & 0b1111) << 1) |
                (((instruction >> 25) & 0b111111) << 5) |
                ((instruction >> 31) << 12);
    }
}
