package org.example.processor;

public class Registers {
    private final int[] registers;

    public enum Address {
        $0, // Always 0
        $HI, // Stores high output of ALU
        $LO, // Stores low output of ALU
        $1,
        $2,
        $3,
        $4,
        $5,
        $6,
        $7,
        $8,
        $9,
        $10,
        $11,
        $12,
        $13
    }

    Registers() {
        registers = new int[16];
    }

    public int getValue(Address address) {
        if (address == Address.$0) return 0;

        return registers[address.ordinal() - 1];
    }

    public void setValue(Address address, int value) {
        if (address == Address.$0) return;

        registers[address.ordinal() - 1] = value;
    }
}
