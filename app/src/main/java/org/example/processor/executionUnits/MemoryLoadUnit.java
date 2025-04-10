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

        switch (instruction) {
            case LBInstruction i -> i.addResult(memory.getByte(i.getAddress(), false));
            case LBUInstruction i -> i.addResult(memory.getByte(i.getAddress(), true));
            case LHWInstruction i -> i.addResult(memory.getHalfWord(i.getAddress(), false));
            case LHWUInstruction i -> i.addResult(memory.getHalfWord(i.getAddress(), true));
            case LWInstruction i -> i.addResult(memory.getWord(i.getAddress()));

            default -> {}
        }
    }
}
