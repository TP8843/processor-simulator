package org.example.processor.instructions;

public class RTypeInstruction implements Instruction {
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
    
    /// Data from first source register for instruction
    public final int rs1;
    
    /// Data from second source register for instruction
    public final int rs2;
    
    /// Destination register for instruction
    public final byte rd;

    /// Result of processing 
    public final int aluResult;
    
    public RTypeInstruction(Opcode opcode, int PC, int rs1, int rs2, byte rd) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.rd = rd;
        this.aluResult = 0;
    }

    public RTypeInstruction(Opcode opcode, int PC, int rs1, int rs2, byte rd, int aluResult) {
        this.opcode = opcode;
        this.PC = PC;
        this.rs1 = rs1;
        this.rs2 = rs2;
        this.rd = rd;
        this.aluResult = aluResult;
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }
}
