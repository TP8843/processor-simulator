package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;
import org.example.processor.instructions.IInstructions.*;
import org.example.processor.instructions.JInstructions.JALInstruction;
import org.example.processor.instructions.JInstructions.JInstruction;
import org.example.processor.instructions.RInstructions.*;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.UInstructions.AUIInstruction;
import org.example.processor.instructions.UInstructions.LUIInstruction;

public class Alu {
    // TODO: Halt in a better way
    private boolean isHalted;
    
    public final Buffer<Instruction> input;
    public final Buffer<Instruction> memoryOutput;
    public final Buffer<Instruction> branchOutput;
    
    public Alu(Buffer<Instruction> input, Buffer<Instruction> memoryOutput, Buffer<Instruction> branchOutput) {
        this.input = input;
        this.memoryOutput = memoryOutput;
        this.branchOutput = branchOutput;
    }

    public void execute() {
        // If input not available or output full, do not run anything
        if(!input.hasValue() || !(memoryOutput.hasSpace() && branchOutput.hasSpace())) {
            return;
        }
        
        Instruction instruction = input.pop().get();
        
        switch (instruction) {
            case LBInstruction i -> i.addResult(i.rs1Data + i.imm);
            case LBUInstruction i -> i.addResult(i.rs1Data + i.imm);
            case LHWInstruction i -> i.addResult(i.rs1Data + i.imm);
            case LHWUInstruction i -> i.addResult(i.rs1Data + i.imm);
            case LWInstruction i -> i.addResult(i.rs1Data + i.imm);
            
            case SLTIInstruction i -> i.addResult(i.rs1Data < i.imm ? 1 : 0);
            case SLTIUInstruction i -> i.addResult(Integer.compareUnsigned(i.rs1Data, i.imm) < 0 ? 1 : 0);
            
            case AddIInstruction i -> i.addResult(i.rs1Data + i.imm);
            
            case ANDIInstruction i -> i.addResult(i.rs1Data & i.imm);
            case ORIInstruction i -> i.addResult(i.rs1Data | i.imm);
            case XORIInstruction i -> i.addResult(i.rs1Data ^ i.imm);
            
            case SLLIInstruction i -> i.addResult(i.rs1Data << (i.imm & 0b11111));
            case SRLIInstruction i -> i.addResult(i.rs1Data >>> (i.imm & 0b11111));
            case SRAIInstruction i -> i.addResult(i.rs1Data >> (i.imm & 0b11111));
            
            case ECallInstruction i -> {}
            case EBreakInstruction i -> {}
            
            case JALInstruction i -> i.addResult(i.getPC() + 4);
            case JALRInstruction i -> i.addResult(i.getPC() + 4);
            
            case ADDInstruction i -> i.addResult(i.getRs1Data() + i.getRs2Data());
            case SUBInstruction i -> i.addResult(i.getRs1Data() - i.getRs2Data());
            case ORInstruction i -> i.addResult(i.getRs1Data() | i.getRs2Data());
            case ANDInstruction i -> i.addResult(i.getRs1Data() & i.getRs2Data());
            case XORInstruction i -> i.addResult(i.getRs1Data() ^ i.getRs2Data());
            
            case SLLInstruction i -> i.addResult(i.getRs1Data() << (i.getRs2Data() & 0b11111));
            case SRLInstruction i -> i.addResult(i.getRs1Data() >>> (i.getRs2Data() & 0b11111));
            case SRAInstruction i -> i.addResult(i.getRs1Data() >> (i.getRs2Data() & 0b11111));
            
            case SLTInstruction i -> i.addResult((i.getRs1Data() < i.getRs2Data()) ? 1 : 0);
            case SLTUInstruction i -> i.addResult(Integer.compareUnsigned(i.getRs1Data(), i.getRs2Data()) < 0 ? 1 : 0);

            case SInstruction i -> i.addResult(i.getRs1Data() + i.imm);
            
            case LUIInstruction i -> i.addResult(i.imm);
            case AUIInstruction i -> i.addResult(i.imm + i.getPC());
            
            default -> throw new IllegalArgumentException("Instruction not valid for ALU: " + instruction);
        }
        
        memoryOutput.put(instruction);

        // Exit program if you detect a jump to yourself
        if (instruction.getType() == Instruction.Type.J_TYPE &&
                ((JInstruction) instruction).getResult() == 0) {
            System.out.println("Reached end of program");
            isHalted = true;
        }
    }
    
    public boolean isHalted() {
        return isHalted;
    }
    
    @Override
    public String toString() {
        return String.format("""
                ALU:
                    Is Halted: %s""", isHalted);
    }
}
