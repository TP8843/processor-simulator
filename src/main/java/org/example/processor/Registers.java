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
        $13,
        $14,
        $15,
        $16,
        $17,
        $18,
        $19,
        $20,
        $21,
        $22,
        $23,
        $24,
        $25,
        $26,
        $27,
        $28,
        $29;
    }

    Registers() {
        registers = new int[32];
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
