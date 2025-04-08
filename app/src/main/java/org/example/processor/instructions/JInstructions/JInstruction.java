package org.example.processor.instructions.JInstructions;

import org.example.processor.Registers;
import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.RegisterWrite;

public abstract class JInstruction implements Instruction, RegisterWrite {
    
    private final Opcode opcode;

    private final int PC;

    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;

    /// Whether a result has been added to the instruction
    private boolean hasResult = false;
    
    /// Result of processing 
    private int result;

    public JInstruction(Opcode opcode, int PC, int imm, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.imm = imm;
        this.rd = rd;
        this.result = 0;
    }

    public void addResult(int result) {
        this.result = result;
        this.hasResult = true;
    }

    @Override
    public boolean hasResult() {
        return hasResult;
    }

    @Override
    public int getResult() {
        return result;
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
        return true;
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

    static public int decodeImmediate(int instruction){
        return (instruction & (0b11111111 << 12)) |
                (((instruction >> 20) & 0b1) << 11) |
                (((instruction >> 21) & 0b1111111111) << 1) |
                ((instruction >> 31) << 20);
    }
    
    static public JInstruction decode(int instruction, int PC, Registers registers) {
        Opcode opcode = Opcode.getOpcode(instruction);
        int imm = decodeImmediate(instruction);
        byte rd = Instruction.decodeRd(instruction);

        return new JALInstruction(opcode, PC, imm, rd);
    }

    @Override
    public String toString() {
        return String.format("""
                J Type Instruction:
                        Opcode: %s
                        PC %s
                        IMM: %s
                        RD: %s
                        ALU Result:  %s""",
                opcode, PC, imm, rd, result);
    }
}
