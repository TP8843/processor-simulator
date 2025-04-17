package org.example.processor.data;

public class Registers {
    /// Zero Register
    public static final byte ZERO = 0;

    /// Return Address
    public static final byte RA = 1;

    /// Stack Pointer
    public static final byte SP = 2;

    /// Global Pointer
    public static final byte GP = 3;

    /// Thread Pointer
    public static final byte TP = 4;

    /// Temporary Register 0
    public static final byte T0 = 5;

    /// Temporary Register 1
    public static final byte T1 = 6;

    /// Temporary Register 2
    public static final byte T2 = 7;

    /// Saved / Frame Pointer
    public static final byte S0 = 8;

    /// Saved Register
    public static final byte S1 = 9;

    /// Function Argument 0 / Return Value
    public static final byte A0 = 10;

    /// Function Argument 1 / Return Value
    public static final byte A1 = 11;

    /// Function Argument 2
    public static final byte A2 = 12;

    /// Function Argument 3
    public static final byte A3 = 13;

    /// Function Argument 4
    public static final byte A4 = 14;

    /// Function Argument 5
    public static final byte A5 = 15;

    /// Function Argument 6
    public static final byte A6 = 16;

    /// Function Argument 7
    public static final byte A7 = 17;

    /// Stores all register values in the processor
    private final int[] registers;
    
    /// Stores if a register is valid for reading (scoreboarding)
    private final boolean[] valid;
    
    public Registers() {
        registers = new int[31];
        valid = new boolean[31];
        
        for (int i = 0; i < 31; i++) {
            valid[i] = true;
        }
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
        if(index > 0 && index <= 31) valid[index - 1] = false;
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
