package org.example.processor.instructions;

import org.example.processor.Registers;

public class JInstruction implements Instruction {
    public enum Type {
        JUMP_AND_LINK;
        
        static public Type decodeType(int instruction) {
            return JUMP_AND_LINK;
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

    public JInstruction(Opcode opcode, Type type, int PC, int imm, byte rd) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = 0;
    }

    public JInstruction(Opcode opcode, Type type, int PC, int imm, byte rd, int aluResult) {
        this.opcode = opcode;
        this.type = type;
        this.PC = PC;
        this.imm = imm;
        this.rd = rd;
        this.aluResult = aluResult;
    }

    public JInstruction addAluResult(int aluResult) {
        return new JInstruction(opcode, type, PC, imm, rd, aluResult);
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
    
    static public JInstruction decode(int instruction, int PC){
        Opcode opcode = Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        int imm = decodeImmediate(instruction);
        byte rd = Instruction.decodeRd(instruction);

        return new JInstruction(opcode, type, PC, imm, rd, imm);
    }

    @Override
    public String toString() {
        return String.format("""
                B Type Instruction:
                    Opcode: %s
                    Type: %s
                    PC %s
                    IMM: %s
                    ALU Result:  %s""",
                opcode, type, PC, imm, aluResult);
    }
}
