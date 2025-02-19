package org.example.processor.instructions;

import org.example.processor.Registers;

public class RInstruction implements Instruction {
    public enum Type {
        ADD,
        SUB,
        XOR,
        OR,
        AND,
        SHIFT_LEFT_LOGICAL,
        SHIFT_RIGHT_LOGICAL,
        SHIFT_RIGHT_ARITHMETIC,
        SET_LESS_THAN,
        SET_LESS_THAN_UNSIGNED;
        
        static public Type decodeType(int instruction) {
            if (Instruction.decodeFunct7(instruction) == 0x00) {
                return switch (Instruction.decodeFunct3(instruction)) {
                    case 0x00 -> ADD;
                    case 0x01 -> SHIFT_LEFT_LOGICAL;
                    case 0x02 -> SET_LESS_THAN;
                    case 0x03 -> SET_LESS_THAN_UNSIGNED;
                    case 0x04 -> XOR;
                    case 0x05 -> SHIFT_RIGHT_LOGICAL;
                    case 0x06 -> OR;
                    case 0x07 -> AND;
                    default -> throw new IllegalArgumentException("Invalid funct: " + instruction);
                };
            } else {
                return switch (Instruction.decodeFunct3(instruction)) {
                    case 0x00 -> SUB;
                    case 0x05 -> SHIFT_RIGHT_ARITHMETIC;
                    default -> throw new IllegalArgumentException("Invalid funct: " + instruction);
                };

            }
        }
    }
    
    private final Opcode opcode;
    
    private final int PC;

    public final Type type;
    
    /// First source register for instruction
    public final byte rs1;
    
    /// Second source register for instruction
    public final byte rs2;
    
    /// True if data has been loaded from registers
    public final boolean hasData;
    
    /// Data for first source register for instruction
    public final int rs1Data;
    
    /// Data for second source regstier for instruction
    public final int rs2Data;
    
    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    public final int aluResult;
    
    public RInstruction(Opcode opcode, Type type, int PC, byte rs1, byte rs2, byte rd) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.hasData = false;
        this.rs1Data = 0;
        this.rs2Data = 0;
        this.rd = rd;
        this.aluResult = 0;
    }

    public RInstruction(Opcode opcode, Type type, int PC, byte rs1, byte rs2, boolean hasData, int rs1Data, int rs2Data, byte rd, int aluResult) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.hasData = hasData;
        this.rs1Data = rs1Data;
        this.rs2Data = rs2Data;
        this.rd = rd;
        this.aluResult = aluResult;
    }

    public RInstruction addAluResult(int aluResult) {
        return new RInstruction(opcode, type, PC, rs1, rs2, hasData, rs1Data, rs2Data, rd, aluResult);
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
    public RInstruction addDataIfAvailable(Registers registers) {
        if (!registers.isValid(rs1) || !registers.isValid(rs2)) return this;
        
        return new RInstruction(
                opcode, 
                type, 
                PC, 
                rs1, 
                rs2, 
                true, 
                registers.getRegister(rs1), 
                registers.getRegister(rs2), 
                rd, 
                aluResult
        );
    }

    @Override
    public void reserveDestination(Registers registers) {
        registers.setInvalid(rd);
    }
    
    static public RInstruction decode(int instruction, int PC, Registers registers) {
        Opcode opcode = Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        byte rs2 = Instruction.decodeRs2(instruction);
        byte rd = Instruction.decodeRd(instruction);
        
        return new RInstruction(opcode, type, PC, rs1, rs2, rd);
    }

    @Override
    public String toString() {
        return String.format("""
                R Type Instruction:
                        Opcode: %s
                        Type: %s
                        PC: %s
                        RS1: %s
                        RS2: %s
                        Has Data: %s
                        RS1 Data: %s
                        RS2 Data: %s
                        RD: %s
                        ALU Result:  %s""",
                opcode, type, PC, rs1, rs2, hasData ? "True" : "False", rs1Data, rs2Data, rd, aluResult);
    }
}
