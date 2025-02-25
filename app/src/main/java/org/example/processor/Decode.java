package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;

import static org.example.processor.instructions.Instruction.*;

public class Decode {
    private final Registers registers;

    public final Buffer<UndecodedInstruction> input;
    public final Buffer<Instruction> output;

    public Decode(Registers registers, Buffer<UndecodedInstruction> input, Buffer<Instruction> output) {
        this.registers = registers;
        this.input = input;
        this.output = output;
    }
    
    public void decode() {
        // Do not run if instruction isn't fetched or there isn't space on the output
        if(!input.hasValue() || !output.hasSpace()) {
            return;
        }
        
        UndecodedInstruction instruction = input.pop().get();
        
        Instruction currentInstruction;
        
        Type type = Opcode.getInstructionType(Opcode.getOpcode(instruction.instruction()));
        
        currentInstruction = switch (type){
            case B_TYPE -> BInstruction.decode(instruction.instruction(), instruction.PC(), registers);
            case I_TYPE -> IInstruction.decode(instruction.instruction(), instruction.PC(), registers);
            case J_TYPE -> JInstruction.decode(instruction.instruction(), instruction.PC(), registers);
            case R_TYPE -> RInstruction.decode(instruction.instruction(), instruction.PC(), registers);
            case S_TYPE -> SInstruction.decode(instruction.instruction(), instruction.PC(), registers);
            case U_TYPE -> UInstruction.decode(instruction.instruction(), instruction.PC(), registers);
        };
        
        // Add data if available, and if data is not available (returned instruction has not got data) output = null
        currentInstruction = currentInstruction.addDataIfAvailable(registers);
        
        output.put(currentInstruction);
    }

    @Override
    public String toString() {
        return String.format("""
                Decode - nothing anymore :0""");
    }
}
