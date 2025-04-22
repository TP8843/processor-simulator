package org.example.processor.buffers;

import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.Optional;

public class ReservationStation implements Buffer<Instruction>, Flushable {
    /// Maximum number of items in reservation station
    public final int size;
    
    private boolean stalled;

    private final ArrayList<Instruction> instructions;
    
    public ReservationStation() {
        this(16);
    }

    public ReservationStation(int size) {
        this.instructions = new ArrayList<>(size);
        this.size = size;
    }

    @Override
    public Optional<Instruction> pop() {
        if(stalled || instructions.isEmpty()) return Optional.empty();

        for(int i = 0; i < instructions.size(); i++){
            Instruction instruction = instructions.get(i);
            if(instruction.hasData()){
                instructions.remove(i);
                return Optional.of(instruction);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Instruction> peek() {
        if(stalled || instructions.isEmpty()) return Optional.empty();

        for (Instruction instruction : instructions) {
            if (instruction.hasData()) {
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
    public boolean put(Instruction value) {
        if(!hasSpace()) return false;

        instructions.add(value);
        return true;
    }

    @Override
    public void flush() {
        instructions.clear();
    }

    /// Adds the data to the currently stored instruction
    public void addData() {
        for(Instruction instruction : instructions){
//            System.out.println("Attempting to add data for " + Integer.toHexString(instruction.getPC()));
            instruction.getDataIfAvailable();
        }
    }

    @Override
    public String toString() {
        return String.format("""
                Current Values: %s
                Stalled: %s""", instructions, stalled ? "True" : "False");
    }
}
