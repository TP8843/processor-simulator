package org.example.processor;

public class Instruction {
    public Opcode opcode;
    
    // Stores the value of the two operands
    public int operand1;
    public int operand2;
    
    public int output;
    public byte writeBackAddress;
    public int storeValue;
    
    Instruction(Opcode opcode, int operand1, int operand2, byte writeBackAddress) {
        this.opcode = opcode;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.writeBackAddress = writeBackAddress;
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
        XOR,
        ANDI,  // Performs bitwise logical AND between a register and an immediate, and stores result in second register
        ORI, // Performs bitwise logical OR between a register and an immediate, and stores result in second register
        XORI,
        
        SLL, // Performs a left shift of a value in a register, using the immediate value, and stores in second register
        SRL, // Performs a right shift of a value in a register, using the immediate value, and stores in second register

        STOR, // Store a register into memory
        LOAD, // Load a value from memory into a register

        SLT, // Sets a register when the first register is less than the second register, and stores in the third
        SLTI, // Sets a register when the first register is less than the immediate value, and stores in the third

        BEQ, // Branch if given register is equal to 0
        BNE, // Branch if given register is not equal to 0
        ;

        public enum DataMode {
            REGISTER_REGISTER,
            REGISTER_IMMEDIATE,
        }

        public DataMode getDataMode() {
            return switch (this) {
                case ADD, SUB, MUL, DIV, AND, OR, XOR, SLT -> DataMode.REGISTER_REGISTER;
                case ADDI, SUBI, ANDI, ORI, XORI, STOR, LOAD, SLL, SRL, SLTI, BEQ, BNE -> DataMode.REGISTER_IMMEDIATE;
            };
        }
        
        // For when I start pipelining
        public int getALUCycles() {
            return switch (this) {
                case ADD, SUB, ADDI, SUBI, AND, OR, XOR, ANDI, ORI, XORI, SLL, SRL, SLT, SLTI, BEQ, BNE -> 1;
                case MUL, DIV -> 2;
                case STOR, LOAD -> 3;
            };
        }
    }

    public static Opcode parseOpcode(String input){
        return switch (input) {
            case "add" -> Opcode.ADD;
            case "sub" -> Opcode.SUB;
            case "mul" -> Opcode.MUL;
            case "div" -> Opcode.DIV;
            case "addi" -> Opcode.ADDI;
            case "subi" -> Opcode.SUBI;
            
            case "and" -> Opcode.AND;
            case "or" -> Opcode.OR;
            case "xor" -> Opcode.XOR;
            case "andi" -> Opcode.ANDI;
            case "ori" -> Opcode.ORI;
            case "xori" -> Opcode.XORI;
            
            case "sll" -> Opcode.SLL;
            case "srl" -> Opcode.SRL;
            
            case "stor" -> Opcode.STOR;
            case "load" -> Opcode.LOAD;
            
            case "beq" -> Opcode.BEQ;
            case "bne" -> Opcode.BNE;
            
            default -> null;
        };
    }
    
    public String toString(){
        return String.format(
                "Instruction: %s \n" +
                "   Store Register: %s\n" + 
                "   Operand 1: %s\n" +
                "   Operand 2: %s\n" +
                "   Current Output: %s\n" +
                "   Store Value: %s",
                opcode, writeBackAddress, operand1, operand2, output, storeValue);
    }
}
