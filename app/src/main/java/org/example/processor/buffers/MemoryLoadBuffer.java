package org.example.processor.buffers;

import org.example.processor.instructions.IInstructions.LoadInstructions.LoadInstruction;
import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.Optional;

public class MemoryLoadBuffer implements Buffer<LoadInstruction>, Flushable{
    /// Maximum number of items in reservation station
    public final int size;

    private boolean stalled;

    private final ArrayList<LoadInstruction> instructions;

    public MemoryLoadBuffer() {
        this(16);
    }

    public MemoryLoadBuffer(int size) {
        this.instructions = new ArrayList<>(size);
        this.size = size;
    }

    @Override
    public Optional<LoadInstruction> pop() {
        if(stalled || instructions.isEmpty()) return Optional.empty();

        for(int i = 0; i < instructions.size(); i++){
            LoadInstruction instruction = instructions.get(i);
            if(instruction.hasMemoryData()){
                instructions.remove(i);
                return Optional.of(instruction);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<LoadInstruction> peek() {
        if(stalled || instructions.isEmpty()) return Optional.empty();

        for (LoadInstruction instruction : instructions) {
            if (instruction.hasMemoryData()) {
                return Optional.of(instruction);
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean hasSpace() {
        return instructions.size() < size;
    }

    @Override
    public boolean hasValue() {
        return (!stalled && !instructions.isEmpty());
    }

    @Override
    public void stall() {
        stalled = true;
    }

    @Override
    public void release() {
        stalled = false;
    }

    @Override
    public boolean put(LoadInstruction value) {
        if(instructions.size() >= size) return false;

        instructions.add(value);
        return true;
    }

    @Override
    public void flush() {
        instructions.clear();
    }

    /// Adds the data to the currently stored instruction
    public void addData() {
        for(LoadInstruction instruction : instructions){
            instruction.getMemoryDataIfAvailable();
        }
    }

    @Override
    public String toString() {
        return String.format("""
                Capacity: %s
                Size: %s
                Current Values: %s
                Stalled: %s""", instructions.size(), size, instructions, stalled ? "True" : "False");
    }
}
