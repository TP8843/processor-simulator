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
        
        builder.append("Register Values:\n");
        
        for (int i = 0; i <= registers.length; i++) {
            if (i == 0) builder.append(String.format("%-2s: %-6s ", i, 0));
            else builder.append(String.format("%-2s: %-6s ", i, registers[i - 1]));
            
            if (i % 8 == 7) builder.append("\n");
        }
        
        return builder.toString();
    }
}
