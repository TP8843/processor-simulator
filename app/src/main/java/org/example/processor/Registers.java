package org.example.processor;

public class Registers {
    private final int[] registers;

    public Registers() {
        registers = new int[31];
    }

    public int getValue(byte address) {
        if (address == 0) return 0;

        return registers[address - 1];
    }

    public void setValue(byte address, int value) {
        if (address == 0) return;

        registers[address - 1] = value;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        for (int i = 0; i < registers.length; i++) {
            builder.append(String.format("%d: %d\n", i + 1, registers[i]));
        }
        
        return builder.toString();
    }
}
