package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.commit.ROB;
import org.example.processor.data.Registers;
import org.example.processor.instructions.*;
import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.JInstructions.JInstruction;
import org.example.processor.instructions.RInstructions.RInstruction;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.UInstructions.UInstruction;

import java.util.Optional;

import static org.example.processor.instructions.Instruction.*;

public class Decode {
    private final Registers registers;

    /// For initialising instructions
    private final ROB rob;

    public final Buffer<UndecodedInstruction> input;
    public final Buffer<Instruction> output;
    public final Buffer<Instruction> branchOutput;

    public Decode(Registers registers,
                  ROB rob,
                  Buffer<UndecodedInstruction> input, 
                  Buffer<Instruction> output, 
                  Buffer<Instruction> branchOutput) {
        this.registers = registers;
        this.rob = rob;
        this.input = input;
        this.output = output;
        this.branchOutput = branchOutput;
    }
    
    public boolean decode() {
        if(!output.hasSpace()) return false;

        Optional<UndecodedInstruction> value = input.pop();
        if(value.isEmpty()) return false;
        UndecodedInstruction instruction = value.get();
        
        try {
            Instruction currentInstruction;
            Type type = Opcode.getInstructionType(Opcode.getOpcode(instruction.instruction()));

            currentInstruction = switch (type) {
                case B_TYPE -> BInstruction.decode(instruction.instruction(), instruction.PC());
                case I_TYPE -> IInstruction.decode(instruction.instruction(), instruction.PC());
                case J_TYPE -> JInstruction.decode(instruction.instruction(), instruction.PC());
                case R_TYPE -> RInstruction.decode(instruction.instruction(), instruction.PC());
                case S_TYPE -> SInstruction.decode(instruction.instruction(), instruction.PC());
                case U_TYPE -> UInstruction.decode(instruction.instruction(), instruction.PC());
                case ENVIRONMENT -> Environment.decode(instruction.instruction(), instruction.PC());
            };

            // Add data if available, and if data is not available (returned instruction has not got data) output = null
            currentInstruction.initOperands(rob);

            output.put(currentInstruction);

            if(currentInstruction.canBranch()){
                branchOutput.put(currentInstruction);
                return true;
            }

        }catch (Exception e){
            System.out.printf("Error decoding instruction at 0x%s: %s%n",
                    Integer.toHexString(instruction.PC()), e.getMessage());

            return false;
        }

        return false;
    }
}
