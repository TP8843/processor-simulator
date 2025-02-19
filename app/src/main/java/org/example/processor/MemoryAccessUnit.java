package org.example.processor;

import org.example.processor.instructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstruction;

public class MemoryAccessUnit {
    private final Memory memory;

    public Instruction input;
    public Instruction output;

    public MemoryAccessUnit(Memory memory) {
        this.memory = memory;
    }

    public void process() {
        // If input null (processor stalled), set output to null and skip processing
        if (input == null) {
            output = null;
            return;
        }
        
        output = switch (input.getType()) {
            case I_TYPE -> processIType((IInstruction) input);
            case S_TYPE -> processSType((SInstruction) input);
            default -> input;
        };
    }

    private IInstruction processIType(IInstruction instruction) {
        int result = switch (instruction.type) {
            case LOAD_BYTE -> memory.getByte(instruction.aluResult, false);
            case LOAD_HALF_WORD -> memory.getHalfWord(instruction.aluResult, false);
            case LOAD_WORD -> memory.getWord(instruction.aluResult);
            case LOAD_BYTE_UNSIGNED -> memory.getByte(instruction.aluResult, true);
            case LOAD_HALF_WORD_UNSIGNED -> memory.getHalfWord(instruction.aluResult, true);
            default -> 0;
        };

        return instruction.addMemoryResult(result);
    }

    private SInstruction processSType(SInstruction instruction) {
        switch (instruction.type) {
            case STORE_BYTE -> memory.storeByte(instruction.aluResult, instruction.rs2);
            case STORE_HALF_WORD -> memory.storeHalfWord(instruction.aluResult, instruction.rs2);
            case STORE_WORD -> memory.storeWord(instruction.aluResult, instruction.rs2);
        }

        return instruction;
    }

    @Override
    public String toString() {
        return String.format("""
                Memory Access Unit:
                    Input: %s
                    Output: %s""", input, output);
    }
}
