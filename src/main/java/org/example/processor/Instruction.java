package org.example.processor;

public class Instruction {
    public Opcode opcode;
    
    // Stores the value of the two operands
    public int operand1;
    public int operand2;
    
    Instruction(Opcode opcode, int operand1, int operand2) {
        this.opcode = opcode;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }

    public enum Opcode {
        ADD, // Add two registers and store in third register
        SUB, // Subtract two registers and store in third register
        ADDI, // Add a register value to an immediate value and store in second register
        SUBI, // Subtract a register value from an immediate value and store in second register

        MUL, // Multiply two registers and store in third register. Result only stored as int
        DIV, // Divide two registers, store remainder in $HI, store divisor $LO

        AND, // Performs bitwise logical AND between two registers, and stores result in third
        OR,  // Performs bitwise logical OR between two registers, and stores result in third
        ANDI,  // Performs bitwise logical AND between a register and an immediate, and stores result in second register
        ORI, // Performs bitwise logical OR between a register and an immediate, and stores result in second register

        SLL, // Performs a left shift of a value in a register, using the immediate value, and stores in second register
        SRL, // Performs a right shift of a value in a register, using the immediate value, and stores in second register

        STO, // Store a register into memory
        LOAD, // Load a value from memory into a register



        BEQ, // Branch if given register is equal to 0
        BNE, // Branch if given register is not equal to 0
        ;

        public enum DataMode {
            REGISTER_REGISTER,
            REGISTER_IMMEDIATE,
        }

        public DataMode getDataMode() {
            return switch (this) {
                case ADD, SUB, MUL, DIV, AND, OR -> DataMode.REGISTER_REGISTER;
                case ADDI, SUBI, ANDI, ORI, STO, LOAD, SLL, SRL, BEQ, BNE -> DataMode.REGISTER_IMMEDIATE;
            };
        }

        public int getALUCycles() {
            return switch (this) {
                case ADD, SUB, ADDI, SUBI, AND, OR, ANDI, ORI, SLL, SRL, BEQ, BNE -> 1;
                case MUL, DIV -> 2;
                case STO, LOAD -> 3;
            };
        }
    }

    public static Opcode parseOpcode(String input){
        return switch (input) {
            case "add" -> Opcode.ADD;
            case "sub" -> Opcode.SUB;
            case "mul" -> Opcode.MUL;
            case "div" -> Opcode.DIV;
            case "sto" -> Opcode.STO;
            case "load" -> Opcode.LOAD;
            default -> null;
        };
    }
}
