package org.example.processor;

public class Registers {
    /// Stores all register values in the processor
    private final int[] registers;
    
    /// Stores if a register is valid for reading (scoreboarding)
    private final boolean[] valid;
    
    public Registers() {
        registers = new int[31];
        valid = new boolean[31];
    }
    
    /// Returns if register value is valid for reading (not waiting for instruction)
    public boolean isValid(int index) {
        // If out of range, return invalid
        if (index < 0 || index > 31) return false;
        
        // If zero register, always valid for reading
        if (index == 0) return true;
        
        return valid[index - 1];
    }
    
    /// Sets a register as invalid (to ensure no erroneous reads)
    public void setInvalid(int index) {
        if(index > 0 && index < 31) valid[index - 1] = false;
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
        
        // Register now valid for reading by another instruction
        valid[index - 1] = true;
    }
    
    @Override
    public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("Register Values:\n");

            for (int i = 0; i <= registers.length; i++) {
                if (i == 0) builder.append(String.format("%-2s[V]: %-6s ", i, 0));
                else builder.append(String.format("%-2s[%s]: %-6s ", i, valid[i - 1]? "V" : "I", registers[i - 1]));

                if (i % 8 == 7) builder.append("\n");
            }

            return builder.toString();
    }
}
