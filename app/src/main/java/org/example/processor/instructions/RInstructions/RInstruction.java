package org.example.processor.instructions.RInstructions;

import org.example.processor.commit.ROB;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.Operand;
import org.example.processor.instructions.RInstructions.multiply.*;
import org.example.processor.instructions.RegisterWrite;

public abstract class RInstruction implements RegisterWrite {
    private final Opcode opcode;
    
    private final int PC;
    
    /// First source register for instruction
    public final Operand rs1;
    
    /// Second source register for instruction
    public final Operand rs2;
    
    /// Destination register for instruction
    public final byte rd;

    /// Whether a result has been added to the instruction
    private boolean hasResult;

    /// Result of processing 
    private int result;
    
    public RInstruction(Opcode opcode, int PC, byte rs1, byte rs2, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = new Operand(rs1);
        this.rs2 = new Operand(rs2);
        this.rd = rd;
        this.result = 0;
    }

    public void addResult(int result) {
        this.result = result;
        hasResult = true;
    }

    public boolean hasResult(){
        return hasResult;
    }

    @Override
    public int getResult() {
        return result;
    }

    @Override
    public boolean isReady() {
        return hasResult();
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
        return rs1.hasData() && rs2.hasData();
    }
    
    @Override
    public void getDataIfAvailable() {
        this.rs1.getDataWhenAvailable();
        this.rs2.getDataWhenAvailable();
    }

    @Override
    public void initOperands(ROB rob) {
        rob.initOperand(this.rs1);
        rob.initOperand(this.rs2);
    }
    
    static public RInstruction decode(int instruction, int PC) {
        Opcode opcode = Opcode.getOpcode(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        byte rs2 = Instruction.decodeRs2(instruction);
        byte rd = Instruction.decodeRd(instruction);

        int funct7 = Instruction.decodeFunct7(instruction);

        if (funct7 == 0x00) {
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x00 -> new ADDInstruction(opcode, PC, rs1, rs2, rd);
                case 0x01 -> new SLLInstruction(opcode, PC, rs1, rs2, rd);
                case 0x02 -> new SLTInstruction(opcode, PC, rs1, rs2, rd);
                case 0x03 -> new SLTUInstruction(opcode, PC, rs1, rs2, rd);
                case 0x04 -> new XORInstruction(opcode, PC, rs1, rs2, rd);
                case 0x05 -> new SRLInstruction(opcode, PC, rs1, rs2, rd);
                case 0x06 -> new ORInstruction(opcode, PC, rs1, rs2, rd);
                case 0x07 -> new ANDInstruction(opcode, PC, rs1, rs2, rd);
                default -> throw new IllegalArgumentException("Invalid funct3: " + instruction);
            };
        } else if (funct7 == 0x01) {
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x00 -> new MULInstruction(opcode, PC, rs1, rs2, rd);
                case 0x01 -> new MULHighInstruction(opcode, PC, rs1, rs2, rd);
                case 0x02 -> new MULHIGHSUInstruction(opcode, PC, rs1, rs2, rd);
                case 0x03 -> new MULHighUInstruction(opcode, PC, rs1, rs2, rd);
                case 0x04 -> new DIVInstruction(opcode, PC, rs1, rs2, rd);
                case 0x05 -> new DIVUInstruction(opcode, PC, rs1, rs2, rd);
                case 0x06 -> new REMInstruction(opcode, PC, rs1, rs2, rd);
                case 0x07 -> new REMUInstruction(opcode, PC, rs1, rs2, rd);
                default -> throw new IllegalArgumentException("Invalid funct3: " + instruction);
            };
        } else if(funct7 == 0x20){
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x00 -> new SUBInstruction(opcode, PC, rs1, rs2, rd);
                case 0x05 -> new SRAInstruction(opcode, PC, rs1, rs2, rd);
                default -> throw new IllegalArgumentException("Invalid funct3: " + instruction);
            };
        } else {
            throw new IllegalArgumentException("Invalid funct7: " + instruction);
        }
    }

    @Override
    public String toString() {
        return String.format("""
                R Type Instruction:
                        Opcode: %s
                        PC: %s
                        Is Ready: %s
                        RS1: %s
                        RS2: %s
                        Has Data: %s
                        RD: %s
                        Has Result: %s
                        Result:  %s""",
                opcode,
                PC,
                isReady() ? "True" : "False",
                rs1,
                rs2,
                hasData() ? "True" : "False",
                rd,
                hasResult ? "True" : "False",
                result);
    }
}
