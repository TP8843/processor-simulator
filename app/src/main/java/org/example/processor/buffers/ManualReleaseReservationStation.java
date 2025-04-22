package org.example.processor.buffers;

import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstructions.SInstruction;

import java.util.Optional;

public class ManualReleaseReservationStation extends ReservationStation {
    /// Current number of items allocated to reservation station
    private int items = 0;

    public ManualReleaseReservationStation(int size) {
        super(size);
    }

    @Override
    public boolean hasSpace() {
        return items < size;
    }

    @Override
    public Optional<Instruction> pop() {
        if(stalled || instructions.isEmpty()) return Optional.empty();

        Instruction current = null;

        for(Instruction instruction : instructions) {
            if(instruction.hasData()){
                if(instruction instanceof SInstruction){
                    instructions.remove(instruction);
                    return Optional.of(instruction);
                } else {
                    current = instruction;
                }

            }
        }

        if(current != null){
            instructions.remove(current);
            return Optional.of(current);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Instruction> peek() {
        if (stalled || instructions.isEmpty()) return Optional.empty();

        Instruction current = null;

        for (Instruction instruction : instructions) {
            if (instruction.hasData()) {
                if (instruction instanceof SInstruction) {
                    return Optional.of(instruction);
                } else {
                    current = instruction;
                }
            }
        }

        if(current != null){
            return Optional.of(current);
        }

        return Optional.empty();
    }

    @Override
    public boolean put(Instruction value) {
        if(!hasSpace()) throw new IllegalStateException("Buffer is full");

        instructions.add(value);
        items += 1;
        return true;
    }

    public void release() {
        if(items > 0) items -= 1;
    }

    @Override
    public void flush() {
        super.flush();
        items = 0;
    }

    @Override
    public String toString() {
        return String.format("""
                Actual Size of Array: %s
                Size: %s
                Capacity: %s
                Current Values: %s
                Stalled: %s""", instructions.size(), items, size, instructions, stalled ? "True" : "False");
    }
    }
