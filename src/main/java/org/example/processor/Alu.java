package org.example.processor;

public class Alu {
    public int execute(Instruction input)
    {
        switch (input.opcode) {
            case ADD -> {
                return input.operand1 + input.operand2;
            }
            
            case SUB -> {
                return input.operand1 - input.operand2;
            }
            
            case MUL -> {
                return input.operand1 * input.operand2;
            }
            
            case DIV -> {
                return input.operand1 / input.operand2;
            }
        }
        
        return 0;
    }
}
