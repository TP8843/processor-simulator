package org.example.processor.instructions;

import org.example.processor.Registers;

public class SInstruction implements Instruction{
    public enum Type {
        STORE_BYTE,
        STORE_HALF_WORD,
        STORE_WORD;
        
        static public Type decodeType(int instruction) {
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x0 -> STORE_BYTE;
                case 0x1 -> STORE_HALF_WORD;
                case 0x2 -> STORE_WORD;
                default -> throw new IllegalArgumentException("Invalid funct for S type instruction: " + instruction);
            };
        }
    }
    
    private final Opcode opcode;

    public final Type type;
    
    private final int PC;

    /// First source register for instruction
    public final byte rs1;

    /// Second source register for instruction
    public final byte rs2;
    
    /// True if data has been fetched from registers
    public final boolean hasData;
    
    /// Data for first source register for instruction
    public final int rs1Data;
    
    /// Data for second source register for instruction
    public final int rs2Data;

    /// Immediate value for instruction
    public final int imm;

    /// Result of processing 
    public final int aluResult;

    public SInstruction(Opcode opcode, Type type, int PC, byte rs1, byte rs2, int imm) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.hasData = false;
        this.rs1Data = 0;
        this.rs2Data = 0;
        this.imm = imm;
        this.aluResult = 0;
    }

    public SInstruction(Opcode opcode, Type type, int PC, byte rs1, byte rs2, boolean hasData, int rs1Data, int rs2Data, int imm, int aluResult) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.hasData = hasData;
        this.rs1Data = rs1Data;
        this.rs2Data = rs2Data;
        this.imm = imm;
        this.aluResult = aluResult;
    }

    public SInstruction addAluResult(int aluResult) {
        return new SInstruction(opcode, type, PC, rs1, rs2, hasData, rs1Data, rs2Data, imm, aluResult);
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }
    
    @Override
    public boolean canBranch() {
        return false;
    }
    
    @Override
    public boolean hasData() {
        return hasData;
    }
    
    @Override
    public SInstruction addDataIfAvailable(Registers registers) {
        if (!registers.isValid(rs1) || !registers.isValid(rs2)) return this;
        
        return new SInstruction(
                opcode,
                type, 
                PC, 
                rs1, 
                rs2, 
                true, 
                registers.getRegister(rs1), 
                registers.getRegister(rs2), 
                imm, 
                aluResult  
        );
    }
    
    static private int decodeImmediate(int instruction){
        return ((instruction >> 7) & 0b11111) |
                (instruction >> 25) << 5;
    }
    
    static public SInstruction decode(int instruction, int PC, Registers registers){
        Opcode opcode = Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        byte rs2 = Instruction.decodeRs2(instruction);
        int imm = decodeImmediate(instruction);
        
        return new SInstruction(opcode, type, PC, rs1, rs2, imm);
    }

    @Override
    public String toString() {
        return String.format("""
                S Type Instruction:
                        Opcode: %s
                        Type: %s
                        PC: %s
                        RS1: %s
                        RS2: %s
                        Has Data: %s
                        RS1 Data: %s
                        RS2 Data: %s
                        IMM: %s
                        ALU Result:  %s""",
                opcode, type, PC, rs1, rs2, hasData ? "True" : "False", rs1Data, rs2Data, imm, aluResult);
    }
}
