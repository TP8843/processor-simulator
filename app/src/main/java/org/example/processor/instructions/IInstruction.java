package org.example.processor.instructions;

import org.example.processor.Registers;

public class IInstruction implements Instruction {
    public enum Type {
        ADDI,
        XORI,
        ORI,
        ANDI,
        SHIFT_LEFT_LOGICAL_IMMEDIATE,
        SHIFT_RIGHT_LOGICAL_IMMEDIATE,
        SHIFT_RIGHT_ARITHMETIC_IMMEDIATE,
        SET_LESS_THAN_IMMEDIATE,
        SET_LESS_THAN_IMMEDIATE_UNSIGNED,
        
        LOAD_BYTE,
        LOAD_HALF_WORD,
        LOAD_HALF_WORD_UNSIGNED,
        LOAD_WORD,
        LOAD_BYTE_UNSIGNED,
        
        JUMP_AND_LINK_REGISTER,
        ENVIRONMENT_CALL,
        ENVIRONMENT_BREAK;

        static public Type decodeType(int instruction) {
            return switch (Instruction.Opcode.getOpcode(instruction)) {
                case LOAD -> decodeLoad(instruction);
                case ARITHMETIC_LOGICAL_IMMEDIATE -> decodeArithmetic(instruction);
                case ENVIRONMENT -> decodeEnvironment(instruction);
                case JUMP_AND_LINK_REGISTER -> JUMP_AND_LINK_REGISTER;
                default -> throw new IllegalArgumentException("invalid opcode for I type instruction " + instruction);
            };
        }

        static private Type decodeEnvironment(int instruction) {
            return switch (Instruction.decodeFunct7(instruction)) {
                case 0x0 -> ENVIRONMENT_CALL;
                case 0x1 -> ENVIRONMENT_BREAK;
                default -> throw new IllegalArgumentException("invalid opcode for I type instruction " + instruction);
            };
        }

        static private Type decodeLoad(int instruction) {
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x0 -> LOAD_BYTE;
                case 0x1 -> LOAD_HALF_WORD;
                case 0x2 -> LOAD_WORD;
                case 0x5 -> LOAD_HALF_WORD_UNSIGNED;
                case 0x4 -> LOAD_BYTE_UNSIGNED;
                default -> throw new IllegalArgumentException("Invalid funct for load: " + instruction);
            };
        }

        static private Type decodeArithmetic(int instruction) {
            int funct3 = Instruction.decodeFunct3(instruction);
            if (Instruction.decodeFunct7(instruction) == 0x20 && funct3 == 0x5) {
                return SHIFT_RIGHT_ARITHMETIC_IMMEDIATE;
            }

            return switch (funct3) {
                case 0x0 -> ADDI;
                case 0x1 -> SHIFT_LEFT_LOGICAL_IMMEDIATE;
                case 0x2 -> SET_LESS_THAN_IMMEDIATE;
                case 0x3 -> SET_LESS_THAN_IMMEDIATE_UNSIGNED;
                case 0x4 -> XORI;
                case 0x5 -> SHIFT_RIGHT_LOGICAL_IMMEDIATE;
                case 0x6 -> ORI;
                case 0x7 -> ANDI;
                default -> throw new IllegalArgumentException("Invalid funct for immediate arith: " + instruction);
            };
        }
    }
    
    private final Opcode opcode;

    public final Type type;
    
    private final int PC;

    /// First source register for instruction
    public final int rs1;
    
    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    public final int aluResult;

    public IInstruction(Opcode opcode, Type type, int PC, int rs1, int imm, byte rd) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = 0;
    }

    public IInstruction(Opcode opcode, Type type, int PC, int rs1, int imm, byte rd, int aluResult) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = aluResult;
    }

    public IInstruction addAluResult(int aluResult) {
        return new IInstruction(opcode, type, PC, rs1, imm, rd, aluResult);
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }
    
    static private int decodeImmediate(int instruction) {
        return (instruction >> 20);
    }
    
    static public IInstruction decode(int instruction, int PC, Registers registers) {
        Opcode opcode = Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        int rs1 = registers.getRegister(Instruction.decodeRs1(instruction));
        int imm = decodeImmediate(instruction);
        byte rd = Instruction.decodeRd(instruction);
        
        return new IInstruction(opcode, type, PC, rs1, imm, rd);
    }
}
