package org.example.processor;

public class CompareUnit {
    public Instruction input;
    private final ProgramCountUpdater programCountUpdater;
    
    public CompareUnit(ProgramCountUpdater programCountUpdater) {
        this.programCountUpdater = programCountUpdater;
    }
    
    public void process() {
        Instruction output = input;
        
        output.output = switch (input.opcode) {
            case BEQ -> (input.operand1 == input.operand2) ? 1 : 0;
            case BNE -> input.operand1 != input.operand2 ? 1 : 0;
            default -> 0;
        };
        
        programCountUpdater.compareInput = output;
    }

    @Override
    public String toString() {
        return "Compare Unit - Input: \n" + input + "-----------------";
    }
}
