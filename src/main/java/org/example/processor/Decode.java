package org.example.processor;

public class Decode {
    private DataStore registers;
    
    Decode(DataStore registers) {
        this.registers = registers;
    }
    
    // Decode an instruction and send it to the ALU for execution
    public Instruction decode(String input)
    {
        String[] args = input.split(" ");

        int address1 = Integer.parseInt(args[1]);
        int address2 = Integer.parseInt(args[2]);

        Instruction.Opcode opcode = Instruction.parseOpcode(args[0]);
        int operand1 = registers.getValue(address1);
        int operand2 = registers.getValue(address2);
        
        return new Instruction(opcode, operand1, operand2);
    }
}
