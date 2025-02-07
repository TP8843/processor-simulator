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
        if (PC == 512) {
            halted = true;
        }
        
//        System.out.println("Reading instruction with PC: " + PC);

        output = memory.getWord(PC);

        if (PC == 120)
            System.out.println("During fetch " + String.format("%32s", Integer.toBinaryString(output)).replace(' ', '0'));

        PC += 4;
    }

    public void updatePC(int PC) {
        this.PC = PC;
    }

    public int getPC() {
        return PC;
    }

    public boolean isHalted() {
        return halted;
    }
}
