package org.example.processor;

public class Decode {
    private final Registers registers;
    public String input;
    
    private final Alu alu;
    private final CompareUnit compareUnit;
    
    Decode(Registers registers, Alu alu, CompareUnit compareUnit) {
        this.registers = registers;
        this.alu = alu;
        this.compareUnit = compareUnit;
    }
    
    // Decode an instruction and send it to the ALU for execution
    public void decode()
    {
        String[] args = input.split(" ");

        byte inputValue1 = Byte.parseByte(args[1]);
        byte inputValue2 = Byte.parseByte(args[2]);

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
        
         Instruction output = new Instruction(opcode, operand1, operand2);
        
        compareUnit.input = output;
        alu.input = output;
    }
}
