package org.example.processor.instructions;

import org.example.processor.Registers;

public class BInstruction implements Instruction {
    public enum Type {
        BRANCH_EQ,
        BRANCH_NE,
        BRANCH_LT,
        BRANCH_GTE,
        BRANCH_LT_UNSIGNED,
        BRANCH_GTE_UNSIGNED;
        

        static public Type decodeType(int instruction) {
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x0 -> BRANCH_EQ;
                case 0x1 -> BRANCH_NE;
                case 0x4 -> BRANCH_LT;
                case 0x5 -> BRANCH_GTE;
                case 0x6 -> BRANCH_LT_UNSIGNED;
                case 0x7 -> BRANCH_GTE_UNSIGNED;
                default -> throw new IllegalArgumentException("Invalid funct for B type instruction: " + instruction);
            };
        }
    }
    
    private final Instruction.Opcode opcode;

    public final Type type;

    private final int PC;

    /// True if register data has been loaded into instruction
    public final boolean hasRegisterData;
    
    /// First source register for instruction
    public final byte rs1;
    
    /// Data for first source register for instruction
    public final int rs1Data;

    /// Second source register for instruction
    public final byte rs2;
    
    /// Data for second source register for instruction
    public final int rs2Data;

    /// Immediate value for instruction
    public final int imm;

    /// Result of processing 
    public final int aluResult;

    public final boolean compareResult;

    public BInstruction(Instruction.Opcode opcode, Type type, int PC, byte rs1, byte rs2, int imm) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.rs1Data = 0;
        this.rs2Data = 0;
        this.hasRegisterData = false;
        this.imm = imm;
        this.aluResult = 0;
        this.compareResult = false;
    }

    public BInstruction(Instruction.Opcode opcode,
                        Type type,
                        int PC,
                        byte rs1,
                        byte rs2,
                        boolean hasRegisterData,
                        int rs1Data,
                        int rs2Data,
                        int imm,
                        int aluResult,
                        boolean compareResult) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.hasRegisterData = hasRegisterData;
        this.rs1Data = rs1Data;
        this.rs2Data = rs2Data;
        this.imm = imm;
        this.aluResult = aluResult;
        this.compareResult = compareResult;
    }

    public BInstruction addAluResult(int aluResult) {
        return new BInstruction(
                opcode, 
                type, 
                PC, 
                rs1, 
                rs2, 
                hasRegisterData, 
                rs1Data, 
                rs2Data, 
                imm, 
                aluResult, 
                compareResult);
    }

    public BInstruction addCompareResult(boolean compareResult) {
        return new BInstruction(
                opcode, 
                type, 
                PC, 
                rs1, 
                rs2, 
                hasRegisterData, 
                rs1Data, 
                rs2Data, 
                imm, 
                aluResult, 
                compareResult);
    }
    
    @Override
    public boolean hasData() {
        return hasRegisterData;
    }
    
    @Override
    public BInstruction addDataIfAvailable(Registers registers) {
        if (!registers.isValid(rs1) || !registers.isValid(rs2)) return this;
        
        return new BInstruction(
                opcode, 
                type, 
                PC, 
                rs1, 
                rs2,
                true,
                registers.getRegister(rs1),
                registers.getRegister(rs2),
                imm,
                aluResult,
                compareResult);
    }
    
    @Override
    public Instruction.Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }
    
    @Override
    public boolean canBranch() {
        return true;
    }

    static private int decodeImmediate(int instruction){
        return (((instruction >> 7) & 0b1) << 11) |
                (((instruction >> 8) & 0b1111) << 1) |
                (((instruction >> 25) & 0b111111) << 5) |
                ((instruction >> 31) << 12);
    }
    
    static public BInstruction decode(int instruction, int PC, Registers registers){
        Instruction.Opcode opcode = Instruction.Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        byte rs2 = Instruction.decodeRs2(instruction);
        int imm = decodeImmediate(instruction);
        
        return new BInstruction(opcode, type, PC, rs1, rs2, imm);
    }
    
    @Override
    public String toString() {
        return String.format("""
                B Type Instruction:
                        Opcode: %s
                        Type: %s
                        PC: %s
                        RS1: %s
                        RS2: %s
                        Has Data: %s
                        RS1 Data: %s
                        RS2 Data: %s
                        IMM: %s
                        ALU Result:  %s
                        Compare Result: %s""",
                opcode, 
                type, 
                PC, 
                rs1, 
                rs2,
                hasRegisterData? "True": "False",
                rs1Data,
                rs2Data,
                imm, 
                aluResult, 
                compareResult);
    }
}
