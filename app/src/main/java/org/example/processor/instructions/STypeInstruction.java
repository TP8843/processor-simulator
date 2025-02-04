package org.example.processor.instructions;

public class STypeInstruction implements Instruction{
    private final Opcode opcode;
    
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

    public STypeInstruction(Opcode opcode, int PC, int rs1, int rs2, int imm) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
        this.aluResult = 0;
    }

    public STypeInstruction(Opcode opcode, int PC, int rs1, int rs2, int imm, int aluResult) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
        this.aluResult = aluResult;
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }
    
    static public int decodeImmediate(int instruction){
        return ((instruction >> 7) & 0b11111) |
                (instruction >> 25);
    }
}
