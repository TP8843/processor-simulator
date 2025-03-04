package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;
import org.example.processor.instructions.IInstructions.*;
import org.example.processor.instructions.JInstructions.JInstruction;
import org.example.processor.instructions.RInstructions.*;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.UInstructions.AUIInstruction;
import org.example.processor.instructions.UInstructions.LUIInstruction;

public class Alu implements InstructionVisitable {
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
        
        instruction.visit(this);

        Instruction outputInstruction = instruction;
        
        memoryOutput.put(outputInstruction);

        // Exit program if you detect a jump to yourself
        if (outputInstruction.getType() == Instruction.Type.J_TYPE &&
                ((JInstruction) outputInstruction).getResult() == 0) {
            System.out.println("Reached end of program");
            isHalted = true;
        }
    }
    
    public boolean isHalted() {
        return isHalted;
    }

    /// Fallback Execute
    public void execute(Instruction instruction){
        throw new IllegalArgumentException("Must be an instruction that uses the ALU");
    }
    
    /// ADD Immediate Execute
    public void execute(AddIInstruction instruction){
        instruction.addResult(instruction.rs1Data + instruction.imm);
    }

    /// Load Byte Execute
    public void execute(LBInstruction instruction){
        instruction.addResult(instruction.rs1Data + instruction.imm);
    }

    /// Load Half Word Execute
    public void execute(LHWInstruction instruction){
        instruction.addResult(instruction.rs1Data + instruction.imm);
    }

    /// Load Word Execute
    public void execute(LWInstruction instruction){
        instruction.addResult(instruction.rs1Data + instruction.imm);
    }

    /// Load Byte Unsigned Execute
    public void execute(LBUInstruction instruction){
        instruction.addResult(instruction.rs1Data + instruction.imm);
    }
    
    /// Load Half Word Unsigned Execute
    public void execute(LHWUInstruction instruction){
        instruction.addResult(instruction.rs1Data + instruction.imm);
    }
    
    /// Set Less Than Immediate Execute
    public void execute(SLTIInstruction instruction){
        instruction.addResult(instruction.rs1Data < instruction.imm ? 1 : 0);
    }

    /// Set Less Than Immediate Unsigned Execute
    public void execute(SLTIUInstruction instruction){
        instruction.addResult(Integer.compareUnsigned(instruction.rs1Data, instruction.imm) < 0 ? 1 : 0);
    }
    
    /// AND Immediate Execute
    public void execute(ANDIInstruction instruction){
        instruction.addResult(instruction.rs1Data & instruction.imm);
    }
    
    /// OR Immediate Execute
    public void execute(ORIInstruction instruction){
        instruction.addResult(instruction.rs1Data | instruction.imm);
    }

    /// XOR Immediate Execute
    public void execute(XORIInstruction instruction){
        instruction.addResult(instruction.rs1Data ^ instruction.imm);
    }
    
    /// Shift Left Logical Immediate Execute
    public void execute(SLLIInstruction instruction){
        instruction.addResult(instruction.rs1Data << (instruction.imm & 0b11111));
    }
    
    /// Shift Right Logical Immediate
    public void execute(SRLIInstruction instruction){
        instruction.addResult(instruction.rs1Data >>> (instruction.imm & 0b11111));
    }

    /// Shift Right Arithmetic Immediate
    public void execute(SRAIInstruction instruction){
        instruction.addResult(instruction.rs1Data >> (instruction.imm & 0b11111));
    }
    
    public void execute(ECallInstruction instruction){}
    public void execute(EBreakInstruction instruction){}
    
    /// ADD Instruction Execute
    public void execute(ADDInstruction instruction){
        instruction.addResult(instruction.getRs1Data() + instruction.getRs2Data());
    }

    /// SUB Instruction Execute
    public void execute(SUBInstruction instruction){
        instruction.addResult(instruction.getRs1Data() - instruction.getRs2Data());
    }

    /// OR Instruction Execute
    public void execute(ORInstruction instruction){
        instruction.addResult(instruction.getRs1Data() | instruction.getRs2Data());
    }

    /// AND Instruction Execute
    public void execute(ANDInstruction instruction){
        instruction.addResult(instruction.getRs1Data() & instruction.getRs2Data());
    }

    /// XOR Instruction Execute
    public void execute(XORInstruction instruction){
        instruction.addResult(instruction.getRs1Data() ^ instruction.getRs2Data());
    }
    
    /// Shift Left Logical Execute
    public void execute(SLLInstruction instruction){
        instruction.addResult(instruction.getRs1Data() << (instruction.getRs2Data() & 0b11111));
    }

    /// Shift Right Logical Execute
    public void execute(SRLInstruction instruction){
        instruction.addResult(instruction.getRs1Data() >>> (instruction.getRs2Data() & 0b11111));
    }

    /// Shift Right Arithmetic Execute
    public void execute(SRAInstruction instruction){
        instruction.addResult(instruction.getRs1Data() >> (instruction.getRs2Data() & 0b11111));
    }
    
    /// Set Less Than Execute
    public void execute(SLTInstruction instruction){
        instruction.addResult((instruction.getRs1Data() < instruction.getRs2Data()) ? 1 : 0);
    }
    
    /// Set Less Than Unsigned Execute
    public void execute(SLTUInstruction instruction){
        instruction.addResult(Integer.compareUnsigned(instruction.getRs1Data(), instruction.getRs2Data()) < 0 ? 1 : 0);
    }
    
    /// Store Instructions Execute
    public void execute(SInstruction instruction){
        instruction.addResult(instruction.getRs1Data() + instruction.imm);
    }
    
    /// Load Upper Immediate Instruction
    public void execute(LUIInstruction instruction){
        instruction.addResult(instruction.imm);
    }
    
    public void execute(AUIInstruction instruction){
        instruction.addResult(instruction.imm + instruction.getPC());
    }
    
    @Override
    public String toString() {
        return String.format("""
                ALU:
                    Is Halted: %s""", isHalted);
    }
}
