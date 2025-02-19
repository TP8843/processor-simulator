package org.example.processor;

import org.example.processor.instructions.*;

import static org.example.processor.instructions.Instruction.*;

public class Decode {
    private final Registers registers;

    public int input;
    public int currentPC;
    public Instruction output;
    
    private Instruction currentInstruction = null;
    private boolean isReady = true;

    public Decode(Registers registers) {
        this.registers = registers;
    }
    
    public boolean isReady() {
        return isReady;
    }
    
    public void decode() {
        // Do not run if instruction isn't fetched
        if(input == 0) {
            output = null;
            return;
        }
        
        Type type = Opcode.getInstructionType(Opcode.getOpcode(input));

        // Only process a new instruction if no current instruction to process
        if (currentInstruction == null) {
            currentInstruction = switch (type){
                case B_TYPE -> BInstruction.decode(input, currentPC, registers);
                case I_TYPE -> IInstruction.decode(input, currentPC, registers);
                case J_TYPE -> JInstruction.decode(input, currentPC, registers);
                case R_TYPE -> RInstruction.decode(input, currentPC, registers);
                case S_TYPE -> SInstruction.decode(input, currentPC, registers);
                case U_TYPE -> UInstruction.decode(input, currentPC, registers);
            };   
        }
        
        // Add data if available, and if data is not available (returned instruction has not got data) output = null
        currentInstruction = currentInstruction.addDataIfAvailable(registers);
        
        if (currentInstruction.hasData()) {
            // Reserve destination now instruction is being issued
            currentInstruction.reserveDestination(registers);
            
            output = currentInstruction;
            currentInstruction = null;
            isReady = true;
        } else {
            output = null;
            isReady = false;
        }
    }

    @Override
    public String toString() {
        return String.format("""
                Decode:
                    Current PC Input: 0x%s
                    Input: 0x%s
                    Current Instruction: %s
                    Output: %s""", Integer.toHexString(currentPC), Integer.toHexString(input), currentInstruction, output);
    }
}
