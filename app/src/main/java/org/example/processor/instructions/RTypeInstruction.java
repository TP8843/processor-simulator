package org.example.processor.instructions;

public class RTypeInstruction implements Instruction {
    private final Opcode opcode;
    
    private final int PC;
    
    // TODO: Add funct
    
    /// Data from first source register for instruction
    public final int rs1;
    
    /// Data from second source register for instruction
    public final int rs2;
    
    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    public final int aluResult;
    
    public RTypeInstruction(Opcode opcode, int PC, int rs1, int rs2, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.rd = rd;
        this.aluResult = 0;
    }

    public RTypeInstruction(Opcode opcode, int PC, int rs1, int rs2, byte rd, int aluResult) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
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
}
