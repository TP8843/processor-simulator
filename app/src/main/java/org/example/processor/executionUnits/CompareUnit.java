package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.BInstructions.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.InstructionVisitable;

public class CompareUnit implements InstructionVisitable {
    public final Buffer<Instruction> input;
    public final Buffer<Instruction> output;

    public CompareUnit(Buffer<Instruction> input, Buffer<Instruction> output) {
        this.input = input;
        this.output = output;
    }
    
    public void execute() {
        // If input is null (processor stalled), do not do any processing
        if (!input.hasValue() || !output.hasSpace()) {
            return;
        }
        
        Instruction instruction = input.pop().get();
        instruction.visit(this);
        output.put(instruction);
    }
    
    public void execute(Instruction instruction) {}
    
    /// Branch Equal To Comparison
    public void execute(BEQInstruction instruction) {
        instruction.addResult(instruction.getRs1Data() == instruction.getRs2Data() ? 1 : 0);
    }
    
    /// Branch Not Equal To Comparison
    public void execute(BNEInstruction instruction) {
        instruction.addResult(instruction.getRs1Data() != instruction.getRs2Data() ? 1 : 0);
    }

    /// Branch Less Than Comparison
    public void execute(BLTInstruction instruction) {
        instruction.addResult(instruction.getRs1Data() < instruction.getRs2Data() ? 1 : 0);
    }

    /// Branch Greater Than or Equal To Comparison
    public void execute(BGTEInstruction instruction) {
        instruction.addResult(instruction.getRs1Data() >= instruction.getRs2Data() ? 1 : 0);
    }

    /// Branch Less Than Unsigned Comparison
    public void execute(BLTUInstruction instruction) {
        instruction.addResult(Integer.compareUnsigned(instruction.getRs1Data(), instruction.getRs2Data()) < 0 ? 1 : 0);
    }

    /// Branch Greater Than or Equal To Unsigned Comparison
    public void execute(BGTEUInstruction instruction) {
        instruction.addResult(Integer.compareUnsigned(instruction.getRs1Data(), instruction.getRs2Data()) >= 0 ? 1 : 0);
    }

    @Override
    public String toString() {
        return String.format("""
                Compare Unit - nothing anymore""");
    }
}
