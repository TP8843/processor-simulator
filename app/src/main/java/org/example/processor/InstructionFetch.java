package org.example.processor;

public class InstructionFetch {
    private final Memory memory;
    private int PC;
    
    /// Output for InstructionFetch
    public int output;

    public InstructionFetch(Memory memory, int PC) {
        this.memory = memory;
        this.PC = PC;
    }

    public void process() {
        output = memory.getWord(PC);

        PC += 4;
    }

    public void updatePC(int PC) {
        this.PC = PC;
    }

    public int getPC() {
        return PC;
    }

    @Override
    public String toString() {
        return String.format("""
                Instruction Fetch:
                    PC = 0x%s
                    Binary Instruction Output: %s
                    Hex Instruction Output: 0x%s""", 
                Integer.toHexString(PC), 
                String.format("%32s", Integer.toBinaryString(output)).replace(' ', '0'),
                Integer.toHexString(output));
    }
}
