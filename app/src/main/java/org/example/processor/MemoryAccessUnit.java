package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.IInstructions.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.InstructionVisitable;
import org.example.processor.instructions.SInstructions.SBInstruction;
import org.example.processor.instructions.SInstructions.SHWInstruction;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.SInstructions.SWInstruction;

public class MemoryAccessUnit implements InstructionVisitable {
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
        
        instruction.visit(this);
        
        output.put(instruction);
    }

    @Override
    public void execute(Instruction instruction) {}
    
    /// Store Byte Instruction
    public void execute(SBInstruction instruction) {
        memory.storeByte(instruction.result, instruction.getRs2Data());
    }

    /// Store Half Word Instruction
    public void execute(SHWInstruction instruction) {
        memory.storeHalfWord(instruction.result, instruction.getRs2Data());
    }
    
    /// Store Word Instruction
    public void execute(SWInstruction instruction) {
        memory.storeWord(instruction.result, instruction.getRs2Data());
    }
    
    /// Load Byte Instruction
    public void execute(LBInstruction instruction) {
        instruction.addResult(memory.getByte(instruction.getResult(), false));
    }

    /// Load Byte Unsigned Instruction
    public void execute(LBUInstruction instruction) {
        instruction.addResult(memory.getByte(instruction.getResult(), true));
    }

    /// Load Half Word Instruction
    public void execute(LHWInstruction instruction) {
        instruction.addResult(memory.getHalfWord(instruction.getResult(), false));
    }

    /// Load Half Word Unsigned Instruction
    public void execute(LHWUInstruction instruction) {
        instruction.addResult(memory.getHalfWord(instruction.getResult(), true));
    }
    
    public void execute(LWInstruction instruction) {
        instruction.addResult(memory.getWord(instruction.getResult()));
    }

    @Override
    public String toString() {
        return String.format("""
                Memory Access Unit:
                    Input: %s
                    Output: %s""", input, output);
    }
}
