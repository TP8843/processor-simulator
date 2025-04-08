package org.example.processor.instructions.UInstructions;

import org.example.processor.Registers;
import org.example.processor.instructions.Instruction;

public abstract class UInstruction implements Instruction {
    private final Opcode opcode;

    private final int PC;

    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    public int result;

    public UInstruction(Opcode opcode, int PC, int imm, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
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
    }
    
    @Override
    public boolean hasData() {
        return true;
    }
    
    @Override
    public void addDataIfAvailable(Registers registers) {}

    @Override
    public void reserveDestination(Registers registers) {
        registers.setInvalid(rd);
    }

    static private int decodeImmediate(int instruction){
        return ((instruction >> 12) << 12);
    }
    
    static public UInstruction decode(int instruction, int PC, Registers registers) {
        Opcode opcode = Opcode.getOpcode(instruction);
        int imm = decodeImmediate(instruction);
        byte rd = Instruction.decodeRd(instruction);

        return switch (Opcode.getOpcode(instruction)) {
            case LOAD_UPPER_IMMEDIATE -> new LUIInstruction(opcode, PC, imm, rd);
            case ADD_UPPER_IMMEDIATE_TO_PC -> new AUIInstruction(opcode, PC, imm, rd);
            default -> throw new IllegalArgumentException("Unknown opcode " + instruction);
        };
    }

    @Override
    public String toString() {
        return String.format("""
                U Type Instruction:
                        Opcode: %s
                        PC: %s
                        RD: %s
                        IMM: %s
                        ALU Result:  %s""",
                opcode, PC, rd, imm, result);
    }
}
