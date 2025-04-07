package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;
import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.JInstructions.JInstruction;
import org.example.processor.instructions.RInstructions.RInstruction;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.UInstructions.UInstruction;

import static org.example.processor.instructions.Instruction.*;

public class Decode {
    private final Registers registers;

    public final Buffer<UndecodedInstruction> input;
    public final Buffer<Instruction> output;
    public final Buffer<Instruction> branchOutput;

    public Decode(Registers registers, 
                  Buffer<UndecodedInstruction> input, 
                  Buffer<Instruction> output, 
                  Buffer<Instruction> branchOutput) {
        this.registers = registers;
        this.input = input;
        this.output = output;
        this.branchOutput = branchOutput;
    }
    
    public void decode() {
        // Do not run if instruction isn't fetched or there isn't space on the output
        if(!input.hasValue() || !output.hasSpace()) return;
        UndecodedInstruction instruction = input.pop().get();
        
        try {
            Instruction currentInstruction;
            Type type = Opcode.getInstructionType(Opcode.getOpcode(instruction.instruction()));

            currentInstruction = switch (type) {
                case B_TYPE -> BInstruction.decode(instruction.instruction(), instruction.PC(), registers);
                case I_TYPE -> IInstruction.decode(instruction.instruction(), instruction.PC(), registers);
                case J_TYPE -> JInstruction.decode(instruction.instruction(), instruction.PC(), registers);
                case R_TYPE -> RInstruction.decode(instruction.instruction(), instruction.PC(), registers);
                case S_TYPE -> SInstruction.decode(instruction.instruction(), instruction.PC(), registers);
                case U_TYPE -> UInstruction.decode(instruction.instruction(), instruction.PC(), registers);
            };

            output.put(currentInstruction);
            branchOutput.put(currentInstruction);
        }catch (Exception e){
            System.out.println(String.format("Error decoding instruction at 0x%s: %s",
                    Integer.toHexString(instruction.PC()), e.getMessage()));
            
            throw e;
        }
    }

    @Override
    public String toString() {
        return String.format("""
                Decode - nothing anymore :0""");
    }
}
