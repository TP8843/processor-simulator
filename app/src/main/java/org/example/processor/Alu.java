package org.example.processor;

import org.example.processor.instructions.*;

public class Alu {
    private boolean isHalted;
    
    public Instruction input;
    public Instruction output;

    public void execute() {
        // If input is null, do not do any processing (currently stalled)
        if(input == null) {
            output = null;
            return;
        }
        
        output = switch (input.getType()) {
            case B_TYPE -> executeBType((BInstruction) input);
            case I_TYPE -> executeIType((IInstruction) input);
            case J_TYPE -> executeJType((JInstruction) input);
            case R_TYPE -> executeRType((RInstruction) input);
            case S_TYPE -> executeSType((SInstruction) input);
            case U_TYPE -> executeUType((UInstruction) input);
        };

        // Exit program if you detect a jump to yourself
        if (output.getType() == Instruction.Type.J_TYPE &&
                ((JInstruction) output).aluResult == 0) {
            System.out.println("Finish execution");
            isHalted = true;
        }
    }
    
    public boolean isHalted() {
        return isHalted;
    }

    static private BInstruction executeBType(BInstruction instruction) {
        int result = instruction.getPC() + instruction.imm;

        return instruction.addAluResult(result);
    }

    static private IInstruction executeIType(IInstruction instruction) {
        int result = switch (instruction.type) {
            case ADDI -> instruction.rs1 + instruction.imm;

            case SET_LESS_THAN_IMMEDIATE ->
                    instruction.rs1 < instruction.imm ? 1 : 0;
            case SET_LESS_THAN_IMMEDIATE_UNSIGNED ->
                    Integer.compareUnsigned(instruction.rs1, instruction.imm) < 0 ? 1 : 0;

            case ANDI -> instruction.rs1 & instruction.imm;
            case ORI -> instruction.rs1 | instruction.imm;
            case XORI -> instruction.rs1 ^ instruction.imm;

            case SHIFT_LEFT_LOGICAL_IMMEDIATE -> instruction.rs1 << (instruction.imm & 0b11111);
            case SHIFT_RIGHT_LOGICAL_IMMEDIATE -> instruction.rs1 >>> (instruction.imm & 0b11111);
            case SHIFT_RIGHT_ARITHMETIC_IMMEDIATE -> instruction.rs1 >> (instruction.imm & 0b11111);

            case LOAD_BYTE, LOAD_HALF_WORD, LOAD_WORD, LOAD_BYTE_UNSIGNED, LOAD_HALF_WORD_UNSIGNED ->
                    instruction.rs1 + instruction.imm;

            // Least significant bit 0
            case JUMP_AND_LINK_REGISTER -> ((instruction.rs1 + instruction.imm) >> 1) << 1;

            case ENVIRONMENT_CALL, ENVIRONMENT_BREAK -> 0;
        };

        return instruction.addAluResult(result);
    }

    static private JInstruction executeJType(JInstruction instruction) {
        int result = switch (instruction.type) {
            case JUMP_AND_LINK -> instruction.imm + instruction.getPC();
        };

        return instruction.addAluResult(result);
    }

    static private RInstruction executeRType(RInstruction instruction) {
        int result = switch (instruction.type) {
            case ADD -> instruction.rs1 + instruction.rs2;
            case SUB -> instruction.rs1 - instruction.rs2;
            case OR -> instruction.rs1 | instruction.rs2;
            case AND -> instruction.rs1 & instruction.rs2;
            case XOR -> instruction.rs1 ^ instruction.rs2;
            case SHIFT_LEFT_LOGICAL -> instruction.rs1 << (instruction.rs2 & 0b11111);
            case SHIFT_RIGHT_LOGICAL -> instruction.rs1 >>> (instruction.rs2 & 0b11111);
            case SHIFT_RIGHT_ARITHMETIC -> instruction.rs1 >> (instruction.rs2 & 0b11111);
            case SET_LESS_THAN -> (instruction.rs1 < instruction.rs2) ? 1 : 0;
            case SET_LESS_THAN_UNSIGNED -> Integer.compareUnsigned(instruction.rs1, instruction.rs2) < 0 ? 1 : 0;
        };

        return instruction.addAluResult(result);
    }

    static private SInstruction executeSType(SInstruction instruction) {
        int result = instruction.rs1 + instruction.imm;

        return instruction.addAluResult(result);
    }

    static private UInstruction executeUType(UInstruction instruction) {
        int result = switch (instruction.type) {
            case LOAD_UPPER_IMMEDIATE -> instruction.imm;
            case ADD_UPPER_IMMEDIATE_TO_PC -> instruction.imm + instruction.getPC();
        };


        return instruction.addAluResult(result);
    }

    @Override
    public String toString() {
        return String.format("""
                ALU:
                    Is Halted: %s
                    Input: %s
                    Output: %s""", isHalted, input, output);
    }
}
