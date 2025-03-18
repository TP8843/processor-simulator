package org.example.processor.buffers;

import org.example.processor.Registers;
import org.example.processor.instructions.Instruction;

import java.util.Optional;

public class ReservationStation implements Buffer<Instruction>, Flushable {
    private final Registers registers;
    
    private boolean stalled;

    private Instruction value;
    
    /// Allows 2 components to get from this class before deleting the value
    // TODO: Remove requirement for this by refactoring compare unit
    private int taken = 2;
    
    public ReservationStation(Registers registers) {
        this.registers = registers;
    }

    @Override
    public Optional<Instruction> pop() {
        if (value != null && value.hasData()) {
            Instruction value = this.value;
            
            if (taken == 2) {
                // Reserve destination in registers before allowing first pop
                value.reserveDestination(registers);
            }
            
            taken -= 1;
            
            if (taken == 0) {
                this.value = null;
            }
            
            return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Instruction> peek() {
        if (value != null) {
            return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public boolean hasSpace() {
        return value == null;
    }

    @Override
    public boolean hasValue() {
        return (stalled == false && value != null && value.hasData());
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
        if (this.value == null) {
            this.value = value;
            taken = 2;
            return true;
        }

        return false;
    }

    @Override
    public void flush() {
        value = null;
    }

    /// Adds the data to the currently stored instruction
    public void addData() {
        // Can only add data if instruction actually in buffer
        if(value == null) return;
        
        System.out.println("Current value in reservation station: " + value);

        value.addDataIfAvailable(registers);
    }

    @Override
    public String toString() {
        return String.format("""
                Current Value: %s
                Taken: %s
                Stalled: %s""", value, taken, stalled ? "True" : "False");
    }
}
