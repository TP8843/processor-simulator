package org.example.processor;

public class Registers {
    private final int[] registers;

    Registers() {
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
}
