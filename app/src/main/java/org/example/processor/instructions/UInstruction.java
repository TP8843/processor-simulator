package org.example.processor.instructions;

import org.example.processor.Registers;

public class UInstruction implements Instruction{
    public enum Type {
        LOAD_UPPER_IMMEDIATE,
        ADD_UPPER_IMMEDIATE_TO_PC;
        
        static public Type decodeType(int instruction) {
            return switch (Opcode.getOpcode(instruction)) {
                case LOAD_UPPER_IMMEDIATE -> LOAD_UPPER_IMMEDIATE;
                case ADD_UPPER_IMMEDIATE_TO_PC -> ADD_UPPER_IMMEDIATE_TO_PC;
                default -> throw new IllegalArgumentException("Unknown opcode " + instruction);
            };
        }
    }
    
    private final Opcode opcode;

    public final Type type;

    private final int PC;

    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    public final int aluResult;

    public UInstruction(Opcode opcode, Type type, int PC, int imm, byte rd) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = 0;
    }

    public UInstruction(Opcode opcode, Type type, int PC, int imm, byte rd, int aluResult) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = aluResult;
    }

    public UInstruction addAluResult(int aluResult) {
        return new UInstruction(opcode, type, PC, imm, rd, aluResult);
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
        return ((instruction >> 12) << 12);
    }
    
    static public UInstruction decode(int instruction, int PC) {
        Opcode opcode = Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        int imm = decodeImmediate(instruction);
        byte rd = Instruction.decodeRd(instruction);

        return new UInstruction(opcode, type, PC, imm, rd);
    }

    @Override
    public String toString() {
        return String.format("""
                B Type Instruction:
                    Opcode: %s
                    Type: %s
                    PC: %s
                    IMM: %s
                    ALU Result:  %s""",
                opcode, type, PC, imm, aluResult);
    }
}
