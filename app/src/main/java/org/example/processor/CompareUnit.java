package org.example.processor;

public class CompareUnit {
    public DecodedInstruction input;
    private final BranchUnit branchUnit;
    
    public CompareUnit(BranchUnit branchUnit) {
        this.branchUnit = branchUnit;
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
        
        branchUnit.compareUnitInput = new DecodedInstruction(
                input.opcode,
                input.currentPC,
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
