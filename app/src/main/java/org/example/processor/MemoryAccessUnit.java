package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstruction;

public class MemoryAccessUnit {
    private final Memory memory;

    public Buffer<Instruction> input;
    public Buffer<Instruction> output;

    public MemoryAccessUnit(Memory memory, Buffer<Instruction> input, Buffer<Instruction> output) {
        this.memory = memory;
        this.input = input;
        this.output = output;
    }

    public void process() {
        // If input null (processor stalled), set output to null and skip processing
        if (!input.hasValue() || !output.hasSpace()) {
            return;
        }
        
        Instruction instruction = input.pop().get();
        
        Instruction outputInstruction = switch (instruction.getType()) {
            case I_TYPE -> processIType((IInstruction) instruction);
            case S_TYPE -> processSType((SInstruction) instruction);
            default -> instruction;
        };
        
        output.put(outputInstruction);
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
            case STORE_BYTE -> memory.storeByte(instruction.aluResult, instruction.rs2Data);
            case STORE_HALF_WORD -> memory.storeHalfWord(instruction.aluResult, instruction.rs2Data);
            case STORE_WORD -> memory.storeWord(instruction.aluResult, instruction.rs2Data);
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
