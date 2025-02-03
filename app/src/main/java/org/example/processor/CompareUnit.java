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
            case BEQ -> (input.aluInput1 == input.aluInput2) ? 1 : 0;
            case BNE -> input.aluInput1 != input.aluInput2 ? 1 : 0;
            case BGEZ -> input.aluInput1 >= 0 ? 1 : 0;
            case BGTZ -> input.aluInput1 > 0 ? 1 : 0;
            case BLEZ -> input.aluInput1 <= 0 ? 1 : 0;
            case BLTZ -> input.aluInput1 < 0 ? 1 : 0;
            default -> 0;
        };
        
        programCountUpdater.compareUnitInput = new Instruction(
                input.opcode, 
                input.aluInput1, 
                input.aluInput2, 
                input.destination, 
                input.aluOutput, 
                input.memoryStoreValue, 
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
