package org.example.processor;

public class InstructionFetch {
    private final Memory memory;
    private int PC;

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
}
