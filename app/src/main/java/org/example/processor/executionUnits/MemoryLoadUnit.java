package org.example.processor.executionUnits;

import org.example.processor.data.Memory;
import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.Instruction;

import java.util.Optional;

public class MemoryLoadUnit {
    /// Memory to load data from
    private final Memory memory;

    /// Input buffer for instructions (generally will come from AGU)
    private final Buffer<LoadInstruction> input;

    public MemoryLoadUnit(Memory memory, Buffer<LoadInstruction> input) {
        this.memory = memory;
        this.input = input;
    }

    public void execute(){
        Optional<LoadInstruction> value = input.pop();
        if(value.isEmpty()) return;
        LoadInstruction instruction = value.get();

        int memoryMask = instruction.getMemoryMask();

        if(memoryMask == 0xffffffff) {
            instruction.addResult(instruction.getResult());
            return;
        }

        int memoryLoad = switch (instruction) {
            case LBInstruction _ -> memory.getByte(instruction.getAddress(), false);
            case LBUInstruction _ -> memory.getByte(instruction.getAddress(), true);
            case LHWInstruction _ -> memory.getHalfWord(instruction.getAddress(), false);
            case LHWUInstruction _ -> memory.getHalfWord(instruction.getAddress(), true);
            case LWInstruction _ -> memory.getWord(instruction.getAddress());

            default -> 0;
        };

        instruction.addResult(instruction.getResult() | memoryLoad & ~memoryMask);
    }
}
