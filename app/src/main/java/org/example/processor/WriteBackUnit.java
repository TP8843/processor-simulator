package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.JInstructions.JInstruction;
import org.example.processor.instructions.RInstructions.RInstruction;
import org.example.processor.instructions.UInstructions.UInstruction;

public class WriteBackUnit {
    private final Registers registers;
    
    public Instruction previous;

    public Buffer<Instruction> input;

    public WriteBackUnit(Registers registers, Buffer<Instruction> input) {
        this.registers = registers;
        this.input = input;
    }

    public boolean writeBack() {
        // If no instruction available, do not process anything
        if (!input.hasValue()) return false;
        
        Instruction instruction = input.pop().get();
        
        previous = instruction;
        
        return switch (instruction.getType()) {
            case I_TYPE -> writeBackIType((IInstruction) instruction);
            case J_TYPE -> writeBackJType((JInstruction) instruction);
            case R_TYPE -> writeBackRType((RInstruction) instruction);
            case U_TYPE -> writeBackUType((UInstruction) instruction);
            default -> false;
        };
    }

    private boolean writeBackIType(IInstruction instruction) {
        switch (instruction.type) {
            case LOAD_BYTE, LOAD_BYTE_UNSIGNED, LOAD_HALF_WORD, LOAD_HALF_WORD_UNSIGNED, LOAD_WORD ->
                registers.setRegister(instruction.rd, instruction.memoryResult);
            case ADDI, ORI, ANDI, XORI, SET_LESS_THAN_IMMEDIATE, SET_LESS_THAN_IMMEDIATE_UNSIGNED, 
                 SHIFT_LEFT_LOGICAL_IMMEDIATE, SHIFT_RIGHT_ARITHMETIC_IMMEDIATE, SHIFT_RIGHT_LOGICAL_IMMEDIATE ->
                registers.setRegister(instruction.rd, instruction.result);
            case JUMP_AND_LINK_REGISTER ->
                registers.setRegister(instruction.rd, instruction.getPC() + 4);
        }
        
        return true;
    }

    private boolean writeBackJType(JInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.getPC() + 4);
        
        return true;
    }

    private boolean writeBackRType(RInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.result);

        return true;
    }

    private boolean writeBackUType(UInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.result);

        System.out.println(instruction);
        return true;
    }

    @Override
    public String toString() {
        return String.format("""
                Write Back Unit - nothing left""", input);
    }
}
