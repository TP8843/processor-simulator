package org.example.processor.instructions.SInstructions;

import org.example.processor.Registers;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.MemoryWrite;

public abstract class SInstruction implements Instruction, MemoryWrite {
    private final Opcode opcode;
    
    private final int PC;

    /// First source register for instruction
    public final byte rs1;

    /// Second source register for instruction
    public final byte rs2;
    
    /// True if data has been fetched from registers
    private boolean hasData;
    
    /// Data for first source register for instruction
    private int rs1Data = 0;
    
    /// Data for second source register for instruction
    private int rs2Data = 0;

    /// Immediate value for instruction
    public final int imm;

    /// Whether the store address has been added for the instruction
    private boolean hasAddress = false;

    /// Final store address
    private int address;

    public SInstruction(Opcode opcode, int PC, byte rs1, byte rs2, int imm) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.imm = imm;
    }

    public void addAddress(int result) {
        this.address = result;
        this.hasAddress = true;
    }

    @Override
    public boolean hasAddress() {
        return hasAddress;
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public int getValue() {
        return this.rs2Data;
    }

    public int getRs1Data() {
        return rs1Data;
    }
    
    public int getRs2Data() {
        return rs2Data;
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
    public void addDataIfAvailable(Registers registers) {
        if (!registers.isValid(rs1) || !registers.isValid(rs2)) return;
        
        this.rs1Data = registers.getRegister(rs1);
        this.rs2Data = registers.getRegister(rs2);
        this.hasData = true;
    }
    
    static private int decodeImmediate(int instruction){
        return ((instruction >> 7) & 0b11111) |
                (instruction >> 25) << 5;
    }
    
    static public SInstruction decode(int instruction, int PC, Registers registers){
        Opcode opcode = Opcode.getOpcode(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        byte rs2 = Instruction.decodeRs2(instruction);
        int imm = decodeImmediate(instruction);

        return switch (Instruction.decodeFunct3(instruction)) {
            case 0x0 -> new SBInstruction(opcode, PC, rs1, rs2, imm);
            case 0x1 -> new SHWInstruction(opcode, PC, rs1, rs2, imm);
            case 0x2 -> new SWInstruction(opcode, PC, rs1, rs2, imm);
            default -> throw new IllegalArgumentException("Invalid funct for S type instruction: " + instruction);
        };

    }

    @Override
    public String toString() {
        return String.format("""
                S Type Instruction:
                        Opcode: %s
                        PC: %s
                        RS1: %s
                        RS2 / Register to Store: %s
                        Has Data: %s
                        RS1 Data: %s
                        RS2 Data / Value to Store: %s
                        IMM: %s
                        Address:  %s""",
                opcode, PC, rs1, rs2, hasData ? "True" : "False", rs1Data, rs2Data, imm, getAddress());
    }
}
