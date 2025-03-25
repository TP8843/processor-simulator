package org.example.processor.buffers;

import org.example.processor.Registers;
import org.example.processor.instructions.Instruction;

import java.util.Optional;

public class ReservationStation implements Buffer<Instruction>, Flushable {
    private final Registers registers;
    
    private boolean stalled;

    private Instruction value;
    
    public ReservationStation(Registers registers) {
        this.registers = registers;
    }

    @Override
    public Optional<Instruction> pop() {
        if (value != null && value.hasData()) {
            Instruction value = this.value;
            
            // Reserve destination in registers before allowing pop
            value.reserveDestination(registers);
            this.value = null;
            
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
                Stalled: %s""", value, stalled ? "True" : "False");
    }
}
