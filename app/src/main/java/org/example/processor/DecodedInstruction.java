package org.example.processor;

public final class DecodedInstruction {
    public final Opcode opcode;
    public final int currentPC;
    
    // Stores the value of the two operands
    public final int aluInput1;
    public final int aluInput2;
    
    public final int aluOutput;
    public final byte destination;
    public final int memoryStoreValue;
    public final int compareUnitOutput;

    DecodedInstruction(Opcode opcode, int currentPC, int aluInput1, int aluInput2, byte destination) {
        this(opcode, currentPC, aluInput1, aluInput2, destination, 0, 0, 0);
    }
    
    DecodedInstruction(Opcode opcode,
                       int currentPC,
                       int aluInput1,
                       int aluInput2,
                       byte destination,
                       int aluOutput,
                       int memoryStoreValue,
                       int compareUnitOutput) {
        this.opcode = opcode;
        this.currentPC = currentPC;
        
        this.aluInput1 = aluInput1;
        this.aluInput2 = aluInput2;
        this.aluOutput = aluOutput;
        
        this.destination = destination;
        this.memoryStoreValue = memoryStoreValue;
        
        this.compareUnitOutput = compareUnitOutput;
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
        BGEZ, // Branch if given register is greater than or equal to 0
        BGTZ, // Branch if given register is greater than 0
        BLEZ, // Branch if given register is less than 0
        BLTZ // Branch if given register is less than 0
        ;

        public enum DataMode {
            REGISTER,
            REGISTER_REGISTER,
            REGISTER_IMMEDIATE,
            REGISTER_VALUE, // Value can be register or immediate
            DATA, // In format const(reg), where final value is const + reg. For load store operations
        }

        public DataMode getDataMode() {
            return switch (this) {
                case ADD, SUB, MUL, DIV, AND, OR, XOR, SLT -> DataMode.REGISTER_REGISTER;
                case ADDI, SUBI, ANDI, ORI, XORI, STOR, LOAD, SLL, SRL, SLTI, BEQ, BNE -> DataMode.REGISTER_IMMEDIATE;
                default -> DataMode.REGISTER_REGISTER;
            };
        }
        
        // For when I start pipelining
        public int getALUCycles() {
            return switch (this) {
                case ADD, SUB, ADDI, SUBI, AND, OR, XOR, ANDI, ORI, XORI, 
                     SLL, SRL, SLT, SLTI, BEQ, BNE, BGTZ, BGEZ, BLEZ, BLTZ -> 1;
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
            case "bgez" -> Opcode.BGEZ;
            case "blez" -> Opcode.BLEZ;
            case "bgtz" -> Opcode.BGTZ;
            case "bltz" -> Opcode.BLTZ;
            
            default -> null;
        };
    }
    
    public String toString(){
        return String.format(
                "Instruction: %s \n" +
                "      Operand 1: %s\n" +
                "      Operand 2: %s\n" +
                "      Current Output: %s\n" +

                "      Store Register: %s\n" +
                "      Memory Store Value: %s\n" +
                "      Compare Unit Output: %s",
                opcode, aluInput1, aluInput2, aluOutput, destination, memoryStoreValue, compareUnitOutput);
    }
}
