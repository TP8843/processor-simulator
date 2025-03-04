package org.example.processor.instructions.IInstructions;

import org.example.processor.Registers;
import org.example.processor.instructions.Instruction;

public abstract class IInstruction implements Instruction {
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
    
    private final int PC;

    /// First source register for instruction
    public final byte rs1;
    
    /// True if the instruction has its data fetched from registers
    public final boolean hasData;
    
    /// Data for first source register for instruction
    public int rs1Data;
    
    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    private int result;

    public IInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.hasData = false;
        this.rs1Data = 0;
        this.imm = imm;
        this.rd = rd;
        this.result = 0;
    }

    public void addResult(int result) {
        this.result = result;
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
        // TODO: Override in JALRInstruction
    }
    
    @Override
    public boolean hasData() {
        return hasData;
    }
    
    @Override 
    public void addDataIfAvailable(Registers registers) {
        if(!registers.isValid(rs1)) return;
        
        this.rs1Data = registers.getRegister(rs1);
    }
    
    @Override
    public void reserveDestination(Registers registers) {
        registers.setInvalid(rd);
    }
    
    static private int decodeImmediate(int instruction) {
        return (instruction >> 20);
    }
    
    static public IInstruction decode(int instruction, int PC, Registers registers) {
        Opcode opcode = Opcode.getOpcode(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        int imm = decodeImmediate(instruction);
        byte rd = Instruction.decodeRd(instruction);
        
        return switch (Instruction.Opcode.getOpcode(instruction)) {
            case LOAD -> decodeLoad(instruction, opcode, PC, rs1, imm, rd);
            case ARITHMETIC_LOGICAL_IMMEDIATE -> decodeArithmetic(instruction, opcode, PC, rs1, imm, rd);
            case ENVIRONMENT -> decodeEnvironment(instruction, opcode, PC, rs1, imm, rd);
            case JUMP_AND_LINK_REGISTER -> new JALRInstruction(opcode, PC, rs1, imm, rd);
            default -> throw new IllegalArgumentException("invalid opcode for I type instruction " + instruction);
        };
    }

    static private IInstruction decodeEnvironment(int instruction, Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        return switch (Instruction.decodeFunct7(instruction)) {
            case 0x0 -> new ECallInstruction(opcode, PC, rs1, imm, rd);
            case 0x1 -> new EBreakInstruction(opcode, PC, rs1, imm, rd);
            default -> throw new IllegalArgumentException("invalid opcode for I type instruction " + instruction);
        };
    }

    static private IInstruction decodeLoad(int instruction, Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        return switch (Instruction.decodeFunct3(instruction)) {
            case 0x0 -> new LBInstruction(opcode, PC, rs1, imm, rd);
            case 0x1 -> new LHWInstruction(opcode, PC, rs1, imm, rd);
            case 0x2 -> new LWInstruction(opcode, PC, rs1, imm, rd);
            case 0x5 -> new LHWUInstruction(opcode, PC, rs1, imm, rd);
            case 0x4 -> new LBUInstruction(opcode, PC, rs1, imm, rd);
            default -> throw new IllegalArgumentException("Invalid funct for load: " + instruction);
        };
    }

    static private IInstruction decodeArithmetic(int instruction, Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        int funct3 = Instruction.decodeFunct3(instruction);
        if (Instruction.decodeFunct7(instruction) == 0x20 && funct3 == 0x5) {
            return new SRAIInstruction(opcode, PC, rs1, imm, rd);
        }

        return switch (funct3) {
            case 0x0 -> new AddIInstruction(opcode, PC, rs1, imm, rd);
            case 0x1 -> new SLLIInstruction(opcode, PC, rs1, imm, rd);
            case 0x2 -> new SLTIInstruction(opcode, PC, rs1, imm, rd);
            case 0x3 -> new SLTIUInstruction(opcode, PC, rs1, imm, rd);
            case 0x4 -> new XORIInstruction(opcode, PC, rs1, imm, rd);
            case 0x5 -> new SRLIInstruction(opcode, PC, rs1, imm, rd);
            case 0x6 -> new ORIInstruction(opcode, PC, rs1, imm, rd);
            case 0x7 -> new ANDIInstruction(opcode, PC, rs1, imm, rd);
            default -> throw new IllegalArgumentException("Invalid funct for immediate arith: " + instruction);
        };
    }

    @Override
    public String toString() {
        return String.format("""
                I Type Instruction:
                        Opcode: %s
                        Type: %s
                        PC: %s
                        RS1: %s
                        Has Data: %s
                        RS1 Data: %s
                        IMM: %s
                        RD: %s
                        ALU Result:  %s
                        Memory Result: %s""",
                opcode, PC, rs1, hasData ? "True" : "False", rs1Data, imm, rd, result);
    }
}
