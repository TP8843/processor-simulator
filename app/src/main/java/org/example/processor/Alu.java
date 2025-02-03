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
        int output = getOutput(input.opcode, input.aluInput1, input.aluInput2);
         
         Instruction memoryAccessorInstruction = new Instruction(
                 input.opcode, 
                 input.aluInput1, 
                 input.aluInput2, 
                 input.destination, 
                 output, 
                 input.memoryStoreValue, 
                 0);

        Instruction programCountUpdaterInstruction = new Instruction(
                input.opcode,
                input.aluInput1,
                input.aluInput2,
                input.destination,
                output,
                input.memoryStoreValue,
                0);
         
         memoryAccessor.input = memoryAccessorInstruction;
         programCountUpdater.aluInput = programCountUpdaterInstruction;
    }

    private static int getOutput(Instruction.Opcode opcode, int operand1, int operand2) {
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
