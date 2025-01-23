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
        ADD,
        SUB,
        MUL,
        DIV,
        MOV,
        STO,
        LOAD;
        
        static public boolean usesAlu(Opcode opcode) {
            switch (opcode) {
                case ADD, DIV, SUB, MUL, STO, LOAD: return true;
                default: return false;
            }
        }
    }

    public static Opcode parseOpcode(String input){
        switch (input) {
            case "add": return Opcode.ADD;
            case "sub": return Opcode.SUB;
            case "mul": return Opcode.MUL;
            case "div": return Opcode.DIV;
            case "mov": return Opcode.MOV;
            case "sto": return Opcode.STO;
            case "load": return Opcode.LOAD;
            default: return null;
        }
    }
}
