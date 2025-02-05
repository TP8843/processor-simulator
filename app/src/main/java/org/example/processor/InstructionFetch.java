package org.example.processor;

public class InstructionFetch {
    private final Memory memory;
    private int PC;
    private boolean halted = false;

    public int output;

    public InstructionFetch(Memory memory, int PC) {
        this.memory = memory;
        this.PC = PC;
    }

    public void process() {
        if (PC == 0) {
            halted = true;
        }

        output = memory.getWord(PC);

        PC += 4;
    }

    public void updatePC(int PC) {
        this.PC = PC - 4;
    }

    public int getPC() {
        return PC;
    }

    public boolean isHalted() {
        return halted;
    }
}
