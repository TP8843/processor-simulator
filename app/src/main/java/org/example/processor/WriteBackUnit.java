package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.JInstructions.JInstruction;
import org.example.processor.instructions.RInstructions.RInstruction;
import org.example.processor.instructions.UInstructions.UInstruction;

public class WriteBackUnit {
    private final Registers registers;
    
    public Instruction previous;

    public Buffer<Instruction> input;

    public WriteBackUnit(Registers registers, Buffer<Instruction> input) {
        this.registers = registers;
        this.input = input;
    }

    /// Write back input instructions. Returns true if instruction processed, else false
    public boolean writeBack() {
        // If no instruction available, do not process anything
        if (!input.hasValue()) return false;
        
        Instruction instruction = input.pop().get();
        System.out.println("Popping for write back: " + instruction);
        
        previous = instruction;
        
        switch (instruction) {
            case JALRInstruction i -> registers.setRegister(i.rd, i.getResult());
            case JInstruction i -> registers.setRegister(i.rd, i.getResult());
            case IInstruction i -> registers.setRegister(i.rd, i.getResult());
            case RInstruction i -> registers.setRegister(i.rd, i.result);
            case UInstruction i -> registers.setRegister(i.rd, i.result);
            default -> {}
        }
        
        return true;
    }

    @Override
    public String toString() {
        return String.format("""
                Write Back Unit - nothing left""", input);
    }
}
