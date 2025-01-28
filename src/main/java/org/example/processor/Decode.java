package org.example.processor;

public class Decode {
    private final Memory registers;
    public String input;
    
    Decode(Memory registers) {
        this.registers = registers;
    }
    
    // Decode an instruction and send it to the ALU for execution
    public Instruction decode()
    {
        String[] args = input.split(" ");

        int inputValue1 = Integer.parseInt(args[1]);
        int inputValue2 = Integer.parseInt(args[2]);

        Instruction.Opcode opcode = Instruction.parseOpcode(args[0]);

        assert opcode != null;
        int operand1, operand2;
        if (opcode.getDataMode() == Instruction.Opcode.DataMode.REGISTER_REGISTER) {
            operand1 = registers.getValue(inputValue1);
            operand2 = registers.getValue(inputValue2);
        } else {
            operand1 = registers.getValue(inputValue1);
            operand2 = Integer.parseInt(args[3]);
        }
        
        return new Instruction(opcode, operand1, operand2);
    }
}
