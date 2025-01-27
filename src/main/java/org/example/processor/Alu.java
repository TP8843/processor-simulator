package org.example.processor;

public class Alu {
    public int execute(Instruction input)
    {
        return switch (input.opcode) {
            // Store and load add an immediate and register value together for the address
            case ADD, STOR, LOAD -> input.operand1 + input.operand2;
            case SUB -> input.operand1 - input.operand2;
            
            case MUL -> input.operand1 * input.operand2;
            case DIV -> input.operand1 / input.operand2;
            case SLL -> input.operand1 << input.operand2;
            case SRL -> input.operand1 >> input.operand2;
            
            case AND, ANDI -> input.operand1 & input.operand2;
            case OR,ORI -> input.operand1 | input.operand2;
            case XOR, XORI -> input.operand1 ^ input.operand2;
            
            default -> 0;
        };
    }
}
