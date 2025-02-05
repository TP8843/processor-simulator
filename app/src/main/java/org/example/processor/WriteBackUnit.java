package org.example.processor;

import org.example.processor.instructions.*;

public class WriteBackUnit {
    private final Registers registers;

    public Instruction input;

    public WriteBackUnit(Registers registers) {
        this.registers = registers;
    }

    public void writeBack() {
        switch (input.getType()) {
            case I_TYPE -> writeBackIType((IInstruction) input);
            case J_TYPE -> writeBackJType((JInstruction) input);
            case R_TYPE -> writeBackRType((RInstruction) input);
            case U_TYPE -> writeBackUType((UInstruction) input);
        };
    }

    private void writeBackIType(IInstruction instruction) {
        switch (instruction.type) {
            case LOAD_BYTE, LOAD_BYTE_UNSIGNED, LOAD_HALF_WORD, LOAD_HALF_WORD_UNSIGNED, LOAD_WORD,
                 SET_LESS_THAN_IMMEDIATE, SET_LESS_THAN_IMMEDIATE_UNSIGNED, SHIFT_LEFT_LOGICAL_IMMEDIATE,
                 SHIFT_RIGHT_ARITHMETIC_IMMEDIATE, SHIFT_RIGHT_LOGICAL_IMMEDIATE ->
                registers.setRegister(instruction.rd, instruction.memoryResult);
            case ADDI, ORI, ANDI, XORI ->
                registers.setRegister(instruction.rd, instruction.aluResult);
            case JUMP_AND_LINK_REGISTER ->
                registers.setRegister(instruction.rd, instruction.getPC() + 4);
        }
    }

    private void writeBackJType(JInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.getPC() + 4);
    }

    private void writeBackRType(RInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.aluResult);
    }

    private void writeBackUType(UInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.aluResult);
    }
}
