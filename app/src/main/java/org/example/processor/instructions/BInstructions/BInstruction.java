package org.example.processor.instructions.BInstructions;

import org.example.processor.commit.ROB;
import org.example.processor.instructions.Branch;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.Operand;

public abstract class BInstruction implements Instruction, Branch {
    private final Instruction.Opcode opcode;

    private final int PC;
    
    /// First source register for instruction
    public final Operand rs1;

    /// Second source register for instruction
    public final Operand rs2;

    /// Immediate value for instruction
    public final int imm;

    /// Whether a comparison result has been added
    private boolean hasResult = false;

    /// Result of comparison
    private boolean result;

    public BInstruction(Instruction.Opcode opcode, int PC, byte rs1, byte rs2, int imm) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = new Operand(rs1);
        this.rs2 = new Operand(rs2);
        this.imm = imm;
        this.result = false;
    }
    
    @Override
    public boolean hasData() {
        return this.rs1.hasData() && this.rs2.hasData();
    }
    
    @Override
    public void getDataIfAvailable() {
        rs1.getDataWhenAvailable();
        rs2.getDataWhenAvailable();
    }

    @Override
    public void initOperands(ROB rob) {
        rob.initOperand(this.rs1);
        rob.initOperand(this.rs2);
    }

    @Override
    public Instruction.Opcode getOpcode() {
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

    /// Adds the result of the comparison to the instruction
    public void addResult(boolean result) {
        this.result = result;
        this.hasResult = true;
    }

    /// Whether a result has been added to the instruction
    public boolean hasResult() { return hasResult; }

    /// The result of the comparison
    public boolean getResult() {
        return result;
    }

    @Override
    public boolean isReady() {
        return hasResult();
    }
    
    static private int decodeImmediate(int instruction){
        return (((instruction >> 7) & 0b1) << 11) |
                (((instruction >> 8) & 0b1111) << 1) |
                (((instruction >> 25) & 0b111111) << 5) |
                ((instruction >> 31) << 12);
    }
    
    static public BInstruction decode(int instruction, int PC){
        Instruction.Opcode opcode = Instruction.Opcode.getOpcode(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        byte rs2 = Instruction.decodeRs2(instruction);
        int imm = decodeImmediate(instruction);
        
        return switch (Instruction.decodeFunct3(instruction)) {
            case 0x0 -> new BEQInstruction(opcode, PC, rs1, rs2, imm);
            case 0x1 -> new BNEInstruction(opcode, PC, rs1, rs2, imm);
            case 0x4 -> new BLTInstruction(opcode, PC, rs1, rs2, imm);
            case 0x5 -> new BGTEInstruction(opcode, PC, rs1, rs2, imm);
            case 0x6 -> new BLTUInstruction(opcode, PC, rs1, rs2, imm);
            case 0x7 -> new BGTEUInstruction(opcode, PC, rs1, rs2, imm);
            default -> throw new IllegalArgumentException("Invalid funct for B type instruction: " + instruction);
        };
    }
    
    @Override
    public String toString() {
        return String.format("""
                B Type Instruction:
                        Opcode: %s
                        PC: %s
                        Is Ready: %s
                        RS1: %s
                        RS2: %s
                        Has Data: %s
                        IMM: %s
                        Has Result: %s
                        Result: %s""",
                opcode,
                PC,
                isReady() ? "True" : "False",
                rs1, 
                rs2,
                hasData() ? "True": "False",
                imm,
                hasResult ? "True" : "False",
                result);
    }
}
