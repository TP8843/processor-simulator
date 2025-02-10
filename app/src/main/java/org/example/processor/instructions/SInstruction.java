package org.example.processor.instructions;

import org.example.processor.Registers;

import javax.naming.ldap.Rdn;

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

    /// Data from first source register for instruction
    public final int rs1;

    /// Data from second source register for instruction
    public final int rs2;

    /// Immediate value for instruction
    public final int imm;

    /// Result of processing 
    public final int aluResult;

    public SInstruction(Opcode opcode, Type type, int PC, int rs1, int rs2, int imm) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
        this.aluResult = 0;
    }

    public SInstruction(Opcode opcode, Type type, int PC, int rs1, int rs2, int imm, int aluResult) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
        this.aluResult = aluResult;
    }

    public SInstruction addAluResult(int aluResult) {
        return new SInstruction(opcode, type, PC, rs1, rs2, imm, aluResult);
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }
    
    static private int decodeImmediate(int instruction){
        return ((instruction >> 7) & 0b11111) |
                (instruction >> 25) << 5;
    }
    
    static public SInstruction decode(int instruction, int PC, Registers registers){
        Opcode opcode = Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        int rs1 = registers.getRegister(Instruction.decodeRs1(instruction));
        int rs2 = registers.getRegister(Instruction.decodeRs2(instruction));
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
                        IMM: %s
                        ALU Result:  %s""",
                opcode, type, PC, rs1, rs2, imm, aluResult);
    }
}
