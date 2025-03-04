package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;

public class Alu {
    // TODO: Halt in a better way
    private boolean isHalted;
    
    public final Buffer<Instruction> input;
    public final Buffer<Instruction> memoryOutput;
    public final Buffer<Instruction> branchOutput;
    
    public Alu(Buffer<Instruction> input, Buffer<Instruction> memoryOutput, Buffer<Instruction> branchOutput) {
        this.input = input;
        this.memoryOutput = memoryOutput;
        this.branchOutput = branchOutput;
    }

    public void execute() {
        // If input not available or output full, do not run anything
        if(!input.hasValue() || !(memoryOutput.hasSpace() && branchOutput.hasSpace())) {
            return;
        }
        
        Instruction instruction = input.pop().get();
        
        // TODO: Make this more elegant

        Instruction outputInstruction = switch (instruction.getType()) {
            case B_TYPE -> executeBType((BInstruction) instruction);
            case I_TYPE -> executeIType((IInstruction) instruction);
            case J_TYPE -> executeJType((JInstruction) instruction);
            case R_TYPE -> executeRType((RInstruction) instruction);
            case S_TYPE -> executeSType((SInstruction) instruction);
            case U_TYPE -> executeUType((UInstruction) instruction);
        };
        
        memoryOutput.put(outputInstruction);
        branchOutput.put(outputInstruction);

        // Exit program if you detect a jump to yourself
        if (outputInstruction.getType() == Instruction.Type.J_TYPE &&
                ((JInstruction) outputInstruction).aluResult == 0) {
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
            case ADDI, LOAD_BYTE, LOAD_HALF_WORD, LOAD_WORD, LOAD_BYTE_UNSIGNED, LOAD_HALF_WORD_UNSIGNED -> 
                    instruction.rs1Data + instruction.imm;

            case SET_LESS_THAN_IMMEDIATE ->
                    instruction.rs1Data < instruction.imm ? 1 : 0;
            case SET_LESS_THAN_IMMEDIATE_UNSIGNED ->
                    Integer.compareUnsigned(instruction.rs1Data, instruction.imm) < 0 ? 1 : 0;

            case ANDI -> instruction.rs1Data & instruction.imm;
            case ORI -> instruction.rs1Data | instruction.imm;
            case XORI -> instruction.rs1Data ^ instruction.imm;

            case SHIFT_LEFT_LOGICAL_IMMEDIATE -> instruction.rs1Data << (instruction.imm & 0b11111);
            case SHIFT_RIGHT_LOGICAL_IMMEDIATE -> instruction.rs1Data >>> (instruction.imm & 0b11111);
            case SHIFT_RIGHT_ARITHMETIC_IMMEDIATE -> instruction.rs1Data >> (instruction.imm & 0b11111);

            // Least significant bit 0
            case JUMP_AND_LINK_REGISTER -> ((instruction.rs1Data + instruction.imm) >> 1) << 1;

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
            case ADD -> instruction.rs1Data + instruction.rs2Data;
            case SUB -> instruction.rs1Data - instruction.rs2Data;
            case OR -> instruction.rs1Data | instruction.rs2Data;
            case AND -> instruction.rs1Data & instruction.rs2Data;
            case XOR -> instruction.rs1Data ^ instruction.rs2Data;
            case SHIFT_LEFT_LOGICAL -> instruction.rs1Data << (instruction.rs2Data & 0b11111);
            case SHIFT_RIGHT_LOGICAL -> instruction.rs1Data >>> (instruction.rs2Data & 0b11111);
            case SHIFT_RIGHT_ARITHMETIC -> instruction.rs1Data >> (instruction.rs2Data & 0b11111);
            case SET_LESS_THAN -> (instruction.rs1Data < instruction.rs2Data) ? 1 : 0;
            case SET_LESS_THAN_UNSIGNED -> Integer.compareUnsigned(instruction.rs1Data, instruction.rs2Data) < 0 ? 1 : 0;
        };

        return instruction.addAluResult(result);
    }

    static private SInstruction executeSType(SInstruction instruction) {
        int result = instruction.rs1Data + instruction.imm;

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
                    Is Halted: %s""", isHalted);
    }
}
