package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.IInstructions.JALRInstruction;
import org.example.processor.instructions.JInstructions.JInstruction;
import org.example.processor.instructions.RInstructions.RInstruction;
import org.example.processor.instructions.UInstructions.UInstruction;

public class WriteBackUnit implements InstructionVisitable{
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
        
        previous = instruction;
        
        instruction.visit(this);
        
        return true;
    }
    
    /// Only Store a Value with Correct Instructions
    public void execute(Instruction instruction) {}
    
    /// Write Back for all I Instructions
    public void execute(IInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.getResult());
    }
    
    /// Write Back for Jump and Link Register Instruction
    public void execute(JALRInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.getPC() + 4);
    }

    /// Write Back for all J Instructions
    public void execute(JInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.getPC() + 4);
    }

    /// Write Back for all R Instructions
    public void execute(RInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.result);
    }

    /// Write Back for all U Instructions
    public void execute(UInstruction instruction) {
        registers.setRegister(instruction.rd, instruction.result);
    }

    @Override
    public String toString() {
        return String.format("""
                Write Back Unit - nothing left""", input);
    }
}
