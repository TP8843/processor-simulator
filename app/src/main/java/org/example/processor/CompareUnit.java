package org.example.processor;

public class CompareUnit {
    public Instruction input;
    private final ProgramCountUpdater programCountUpdater;
    
    public CompareUnit(ProgramCountUpdater programCountUpdater) {
        this.programCountUpdater = programCountUpdater;
    }
    
    public void process() {
        int output;
        
        output = switch (input.opcode) {
            case BEQ -> (input.operand1 == input.operand2) ? 1 : 0;
            case BNE -> input.operand1 != input.operand2 ? 1 : 0;
            default -> 0;
        };
        
        programCountUpdater.compareInput = new Instruction(
                input.opcode, 
                input.operand1, 
                input.operand2, 
                input.writeBackAddress, 
                input.output, 
                input.storeValue, 
                output
        );
    }

    @Override
    public String toString() {
        return String.format("""
                Compare Unit:
                    Input: %s""", input);
    }
}
