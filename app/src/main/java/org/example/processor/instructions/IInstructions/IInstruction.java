package org.example.processor.instructions.IInstructions;

import org.example.processor.commit.ROB;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.Operand;
import org.example.processor.instructions.RegisterWrite;

public abstract class IInstruction implements RegisterWrite {
    private final Opcode opcode;
    
    private final int PC;

    /// First source register for instruction
    public final Operand rs1;
    
    /// Immediate value for instruction
    public final int imm;

    /// Destination register for instruction
    public final byte rd;

    /// Whether a result has been added
    private boolean hasResult;

    /// Result of processing 
    protected int result;

    public IInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = new Operand(rs1);
        this.imm = imm;
        this.rd = rd;
        this.result = 0;
    }

    public void addResult(int result) {
        this.result = result;
        hasResult = true;
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
    public byte getDestination() {
        return rd;
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
        return rs1.hasData();
    }
    
    @Override 
    public void getDataIfAvailable() {
        this.rs1.getDataWhenAvailable();
    }

    @Override
    public void initOperands(ROB rob) {
        rob.initOperand(this.rs1);
    }

    
    static private int decodeImmediate(int instruction) {
        return (instruction >> 20);
    }
    
    static public IInstruction decode(int instruction, int PC) {
        Opcode opcode = Opcode.getOpcode(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        int imm = decodeImmediate(instruction);
        byte rd = Instruction.decodeRd(instruction);

        return switch (Instruction.Opcode.getOpcode(instruction)) {
            case LOAD -> decodeLoad(instruction, opcode, PC, rs1, imm, rd);
            case ARITHMETIC_LOGICAL_IMMEDIATE -> decodeArithmetic(instruction, opcode, PC, rs1, imm, rd);
            case JUMP_AND_LINK_REGISTER -> new JALRInstruction(opcode, PC, rs1, imm, rd);
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
                        PC: %s
                        Is Ready: %s
                        RS1: %s
                        Has Data: %s
                        IMM: %s
                        RD: %s
                        Has Result: %s
                        Result:  %s""",
                opcode,
                PC,
                isReady() ? "True" : "False",
                rs1,
                hasData() ? "True" : "False",
                imm,
                rd,
                hasResult ? "True" : "False",
                result);
    }
}
