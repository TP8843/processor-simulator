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
        // TODO: Copy implementation from MIPS version
        return "TODO: Register toString method";
    }
}
