package org.example.processor;

import java.util.Arrays;

public class Decode {
    private final Registers registers;
    public String input;
    
    private final Alu alu;
    private final CompareUnit compareUnit;
    
    public Decode(Registers registers, Alu alu, CompareUnit compareUnit) {
        this.registers = registers;
        this.alu = alu;
        this.compareUnit = compareUnit;
    }
    
    // Decode an instruction and send it to the ALU for execution
    public void decode()
    {
        String[] args = input.split(" ");

        Instruction.Opcode opcode = Instruction.parseOpcode(args[0]);
        String[] operands = Arrays
                .stream(args[1].split(","))
                .map(String::trim)
                .map((value) -> value.startsWith("$") ? value.substring(1) : value)
                .toArray(String[]::new);

        byte writeBackAddress = Byte.parseByte(operands[0]);
        byte inputValue1 = Byte.parseByte(operands[1]);
        byte inputValue2 = Byte.parseByte(operands[2]);

        assert opcode != null;
        
        int operand1, operand2;
        operand1 = inputValue1;
        
        if (opcode.getDataMode() == Instruction.Opcode.DataMode.REGISTER_REGISTER) 
            operand2 = registers.getValue(inputValue2);
        else 
            operand2 = inputValue2;
        
         Instruction outputAlu = new Instruction(opcode, operand1, operand2, writeBackAddress);
        Instruction outputCompare = new Instruction(opcode, operand1, operand2, writeBackAddress);

        if (opcode == Instruction.Opcode.STOR) {
            System.out.println("Store instruction");
            outputAlu.storeValue = registers.getValue(writeBackAddress);
            outputCompare.storeValue = registers.getValue(writeBackAddress);
        }

        compareUnit.input = outputAlu;
        alu.input = outputCompare;
    }

    @Override
    public String toString() {
        return "Decode - Input: \n" + input + "-----------------";
    }
}
