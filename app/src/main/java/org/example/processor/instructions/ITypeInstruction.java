package org.example.processor.instructions;

public class ITypeInstruction implements Instruction {
    private final Opcode opcode;
    
    private final int PC;
    
    // TODO: Add funct

    /// Data from first source register for instruction
    public final int rs1;
    
    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    public final int aluResult;

    public ITypeInstruction(Opcode opcode, int PC, int rs1, int imm, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = 0;
    }

    public ITypeInstruction(Opcode opcode, int PC, int rs1, int imm, byte rd, int aluResult) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
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
    
    public static int decodeImmediate(int instruction) {
        return (instruction >> 20);
    }
}
