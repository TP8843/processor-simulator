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
    private final Buffer<Instruction> input;

    public MemoryLoadUnit(Memory memory, Buffer<Instruction> input) {
        this.memory = memory;
        this.input = input;
    }

    public void execute(){
        Optional<Instruction> value = input.pop();
        if(value.isEmpty()) return;
        Instruction instruction = value.get();

        if(instruction instanceof LoadInstruction i){
            int memoryMask = i.getMemoryMask();

            if(memoryMask == 0xffffffff) return;

            int memoryLoad = switch (instruction) {
                case LBInstruction _ -> memory.getByte(i.getAddress(), false);
                case LBUInstruction _ -> memory.getByte(i.getAddress(), true);
                case LHWInstruction _ -> memory.getHalfWord(i.getAddress(), false);
                case LHWUInstruction _ -> memory.getHalfWord(i.getAddress(), true);
                case LWInstruction _ -> memory.getWord(i.getAddress());

                default -> 0;
            };

            i.addResult(memoryLoad & ~memoryMask);
        }
    }
}
