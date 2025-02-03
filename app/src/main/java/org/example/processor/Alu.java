package org.example.processor;

public class Alu {
    public DecodedInstruction input;
    private final MemoryAccessor memoryAccessor;
    private final BranchUnit branchUnit;
    
    public Alu(MemoryAccessor memoryAccessor, BranchUnit branchUnit) {
        this.memoryAccessor = memoryAccessor;
        this.branchUnit = branchUnit;
    }
    
    public void execute() {
        int output = getOutput(input.opcode, input.aluInput1, input.aluInput2);
         
         DecodedInstruction memoryAccessorDecodedInstruction = new DecodedInstruction(
                 input.opcode,
                 input.currentPC,
                 input.aluInput1, 
                 input.aluInput2, 
                 input.destination, 
                 output, 
                 input.memoryStoreValue, 
                 0);

        DecodedInstruction programCountUpdaterDecodedInstruction = new DecodedInstruction(
                input.opcode,
                input.currentPC,
                input.aluInput1,
                input.aluInput2,
                input.destination,
                output,
                input.memoryStoreValue,
                0);
         
         memoryAccessor.input = memoryAccessorDecodedInstruction;
         branchUnit.aluInput = programCountUpdaterDecodedInstruction;
    }

    private static int getOutput(DecodedInstruction.Opcode opcode, int operand1, int operand2) {
        int output;

        switch (opcode) {
           // Store and load add an immediate and register value together for the address
           case ADD, ADDI, STOR, LOAD -> output = operand1 + operand2;
           case SUB, SUBI -> output = operand1 - operand2;
           
           case MUL -> output = operand1 * operand2;
           case DIV -> output = operand1 / operand2;
           case SLL -> output = operand1 << operand2;
           case SRL -> output = operand1 >> operand2;
           
           case AND, ANDI -> output = operand1 & operand2;
           case OR,ORI -> output = operand1 | operand2;
           case XOR, XORI -> output = operand1 ^ operand2;
           
           default -> output = 0;
       }
        return output;
    }

    @Override
    public String toString() {
        return String.format("""
                ALU:
                    Input: %s
                """, input);
    }
}
