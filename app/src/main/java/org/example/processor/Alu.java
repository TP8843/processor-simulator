package org.example.processor;

public class Alu {
    public Instruction input;
    private final MemoryAccessor memoryAccessor;
    private final ProgramCountUpdater programCountUpdater;
    
    public Alu(MemoryAccessor memoryAccessor, ProgramCountUpdater programCountUpdater) {
        this.memoryAccessor = memoryAccessor;
        this.programCountUpdater = programCountUpdater;
    }
    
    public void execute() {
         int output;
         
         switch (input.opcode) {
            // Store and load add an immediate and register value together for the address
            case ADD, ADDI, STOR, LOAD -> output = input.operand1 + input.operand2;
            case SUB, SUBI -> output = input.operand1 - input.operand2;
            
            case MUL -> output = input.operand1 * input.operand2;
            case DIV -> output = input.operand1 / input.operand2;
            case SLL -> output = input.operand1 << input.operand2;
            case SRL -> output = input.operand1 >> input.operand2;
            
            case AND, ANDI -> output = input.operand1 & input.operand2;
            case OR,ORI -> output = input.operand1 | input.operand2;
            case XOR, XORI -> output = input.operand1 ^ input.operand2;
            
            default -> output = 0;
        };
         
         Instruction memoryAccessorInstruction = new Instruction(
                 input.opcode, 
                 input.operand1, 
                 input.operand2, 
                 input.writeBackAddress, 
                 output, 
                 input.storeValue, 
                 0);

        Instruction programCountUpdaterInstruction = new Instruction(
                input.opcode,
                input.operand1,
                input.operand2,
                input.writeBackAddress,
                output,
                input.storeValue,
                0);
         
         memoryAccessor.input = memoryAccessorInstruction;
         programCountUpdater.aluInput = programCountUpdaterInstruction;
    }

    @Override
    public String toString() {
        return String.format("""
                ALU:
                    Input: %s
                """, input);
    }
}
