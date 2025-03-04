package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.UndecodedInstruction;

public class InstructionFetch {
    private final Memory memory;
    private int PC;
    
    /// Output for InstructionFetch
    public final Buffer<UndecodedInstruction> output;

    public InstructionFetch(Memory memory, int PC, Buffer<UndecodedInstruction> buffer) {
        this.memory = memory;
        this.PC = PC;
        this.output = buffer;
    }

    public void process() {
        if(output.hasSpace()) {
            output.put(new UndecodedInstruction(PC, memory.getWord(PC)));
            PC += 4;
        }
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
                    PC = 0x%s""", 
                Integer.toHexString(PC));
    }
}
