package org.example.processor.instructions;

public interface Instruction {
    public enum Type {
        B_TYPE,
        I_TYPE,
        J_TYPE,
        R_TYPE,
        S_TYPE,
        U_TYPE
    }
    
    public enum Opcode {
        ARITHMETIC_LOGICAL,
        ARITHMETIC_LOGICAL_IMMEDIATE,
        LOAD,
        STORE,
        BRANCH,
        JUMP_AND_LINK,
        JUMP_AND_LINK_REGISTER,
        LOAD_UPPER_IMMEDIATE,
        ADD_UPPER_IMMEDIATE_TO_PC,
        ENVIRONMENT;
        
        public static Opcode getOpcode(int instruction){
            return switch (instruction & 0b1111111) {
                case 0b0110011 -> ARITHMETIC_LOGICAL;
                case 0b0010011 -> ARITHMETIC_LOGICAL_IMMEDIATE;
                case 0b0000011 -> LOAD;
                case 0b0100011 -> STORE;
                case 0b1100011 -> BRANCH;
                case 0b1101111 -> JUMP_AND_LINK;
                case 0b1100111 -> JUMP_AND_LINK_REGISTER;
                case 0b0110111 -> LOAD_UPPER_IMMEDIATE;
                case 0b0010111 -> ADD_UPPER_IMMEDIATE_TO_PC;
                case 0b1110011 -> ENVIRONMENT;
                default -> throw new IllegalArgumentException("Unknown opcode " + instruction);
            };
        }
        
        public static Type getInstructionType(Opcode opcode){
            return switch (opcode){
                case ARITHMETIC_LOGICAL -> Type.R_TYPE;
                case ARITHMETIC_LOGICAL_IMMEDIATE, ENVIRONMENT, JUMP_AND_LINK_REGISTER, LOAD -> Type.I_TYPE;
                case STORE -> Type.S_TYPE;
                case BRANCH -> Type.B_TYPE;
                case JUMP_AND_LINK -> Type.J_TYPE;
                case LOAD_UPPER_IMMEDIATE, ADD_UPPER_IMMEDIATE_TO_PC -> Type.U_TYPE;
            };
        }
    }


    /// Gets the current opcode for the instruction
    public Opcode getOpcode();
    
    default public Type getType() {
        return Opcode.getInstructionType(getOpcode());   
    }
    
    /// Gets the current PC for the instruction
    public int getPC();

    static public byte decodeRs1(int instruction) {
        return (byte) ((instruction >> 15) & 0b11111);
    }

    static public byte decodeRs2(int instruction) {
        return (byte) ((instruction >> 20) & 0b11111);
    }

    static public byte decodeRd(int instruction) {
        return (byte) ((instruction >> 7) & 0b11111);
    }
}
