package org.example.processor;

import java.util.Arrays;

public class Decode {
    private final Registers registers;
    
    // Input values for decode
    public String input;
    public int inputPC;
    
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

        DecodedInstruction.Opcode opcode = DecodedInstruction.parseOpcode(args[0]);
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
        
        if (opcode.getDataMode() == DecodedInstruction.Opcode.DataMode.REGISTER_REGISTER) 
            operand2 = registers.getValue(inputValue2);
        else 
            operand2 = inputValue2;
        
         DecodedInstruction outputAlu;
         
        DecodedInstruction outputCompare;

        if (opcode == DecodedInstruction.Opcode.STOR) {
            outputAlu = new DecodedInstruction(
                    opcode,
                    inputPC,
                    operand1,
                    operand2,
                    writeBackAddress,
                    0,
                    registers.getValue(writeBackAddress),
                    0
            );

            outputCompare = new DecodedInstruction(
                    opcode,
                    inputPC,
                    operand1, 
                    operand2, 
                    writeBackAddress, 
                    0, 
                    registers.getValue(writeBackAddress), 
                    0
            );
        } else {
            outputAlu = new DecodedInstruction(
                    opcode, inputPC, operand1, operand2, writeBackAddress);
            
            outputCompare = new DecodedInstruction(
                    opcode, inputPC, operand1, operand2, writeBackAddress);
        }

        compareUnit.input = outputAlu;
        alu.input = outputCompare;
    }
    
//    public void newDecode() {
//        String[] tokens = input.split(" ");
//        DecodedInstruction.Opcode opcode = DecodedInstruction.parseOpcode(args[0]);
//        
//        
//    }

    @Override
    public String toString() {
        return String.format("""
                Decode:
                    Input: %s""", input);
    }
}
