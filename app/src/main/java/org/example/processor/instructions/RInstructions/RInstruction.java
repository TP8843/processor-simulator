package org.example.processor.instructions.RInstructions;

import org.example.processor.commit.ROB;
import org.example.processor.data.Registers;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.Operand;
import org.example.processor.instructions.RegisterWrite;

public abstract class RInstruction implements RegisterWrite {
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
    
    static public RInstruction decode(int instruction, int PC, Registers registers) {
        Opcode opcode = Opcode.getOpcode(instruction);
        Type type = Type.decodeType(instruction);
        byte rs1 = Instruction.decodeRs1(instruction);
        byte rs2 = Instruction.decodeRs2(instruction);
        byte rd = Instruction.decodeRd(instruction);

        if (Instruction.decodeFunct7(instruction) == 0x00) {
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x00 -> new ADDInstruction(opcode, PC, rs1, rs2, rd);
                case 0x01 -> new SLLInstruction(opcode, PC, rs1, rs2, rd);
                case 0x02 -> new SLTInstruction(opcode, PC, rs1, rs2, rd);
                case 0x03 -> new SLTUInstruction(opcode, PC, rs1, rs2, rd);
                case 0x04 -> new XORInstruction(opcode, PC, rs1, rs2, rd);
                case 0x05 -> new SRLInstruction(opcode, PC, rs1, rs2, rd);
                case 0x06 -> new ORInstruction(opcode, PC, rs1, rs2, rd);
                case 0x07 -> new ANDInstruction(opcode, PC, rs1, rs2, rd);
                default -> throw new IllegalArgumentException("Invalid funct: " + instruction);
            };
        } else {
            return switch (Instruction.decodeFunct3(instruction)) {
                case 0x00 -> new SUBInstruction(opcode, PC, rs1, rs2, rd);
                case 0x05 -> new SRAInstruction(opcode, PC, rs1, rs2, rd);
                default -> throw new IllegalArgumentException("Invalid funct: " + instruction);
            };
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
