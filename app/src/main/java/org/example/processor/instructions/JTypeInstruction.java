package org.example.processor.instructions;

public class JTypeInstruction implements Instruction {
    private final Opcode opcode;

    private final int PC;

    // TODO: Add funct

    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;
    
    /// Result of processing 
    public final int aluResult;

    public JTypeInstruction(Opcode opcode, int PC, int imm, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = 0;
    }

    public JTypeInstruction(Opcode opcode, int PC, int imm, byte rd, int aluResult) {
        this.opcode = opcode;
        this.PC = PC;
        this.imm = imm;
        this.rd = rd;
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
        return (((instruction >> 12) & 0b11111111) << 12) |
                (((instruction >> 20) & 0b1) << 11) |
                (((instruction >> 21) & 0b1111111111) << 1) |
                ((instruction >> 31) << 20);
    }
}
