package org.example.processor;

public class Registers {
    private final int[] registers;
    
    public Registers() {
        registers = new int[31];
    }
    
    /// Returns the value currently stored inside the register
    public int getRegister(int index) {
        // If out of range or zero register
        if (index <= 0 || index > 31) return 0;
        
        // Account for zero register
        return registers[index - 1];
    }
    
    /// Store the value inside the corresponding register
    public void setRegister(byte index, int value) {
        // Do not do anything if zero register or outside range
        if (index <= 0 || index > 31) return;
        
        // Account for zero register
        registers[index - 1] = value;
    }
    
    @Override
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
