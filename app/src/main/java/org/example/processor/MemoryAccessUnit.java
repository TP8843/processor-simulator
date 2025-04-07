package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.IInstructions.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstructions.SBInstruction;
import org.example.processor.instructions.SInstructions.SHWInstruction;
import org.example.processor.instructions.SInstructions.SWInstruction;

public class MemoryAccessUnit {
    private final Memory memory;

    public Buffer<Instruction> input;
    public Buffer<Instruction> output;

    public MemoryAccessUnit(Memory memory, Buffer<Instruction> input, Buffer<Instruction> output) {
        this.memory = memory;
        this.input = input;
        this.output = output;
    }

    public void process() {
        // If input null (processor stalled), set output to null and skip processing
        if (!input.hasValue() || !output.hasSpace()) {
            return;
        }
        
        Instruction instruction = input.pop().get();
        
        switch (instruction) {
            case SBInstruction i -> memory.storeByte(i.result, i.getRs2Data());
            case SHWInstruction i -> memory.storeHalfWord(i.result, i.getRs2Data());
            case SWInstruction i -> {
                System.out.println("Storing value at 0x" + Integer.toHexString(i.getPC()));
                memory.storeWord(i.result, i.getRs2Data());
            }
            
            case LBInstruction i -> i.addResult(memory.getByte(i.getResult(), false));
            case LBUInstruction i -> i.addResult(memory.getByte(i.getResult(), true));
            case LHWInstruction i -> i.addResult(memory.getHalfWord(i.getResult(), false));
            case LHWUInstruction i -> i.addResult(memory.getHalfWord(i.getResult(), true));
            case LWInstruction i -> i.addResult(memory.getWord(i.getResult()));
            
            default -> {}
        }
        
        output.put(instruction);
    }

    @Override
    public String toString() {
        return String.format("""
                Memory Access Unit:
                    Input: %s
                    Output: %s""", input, output);
    }
}
