package org.example.processor;

import org.example.processor.instructions.*;

import static org.example.processor.instructions.Instruction.*;

public class Decode {
    private final Registers registers;

    public int input;
    public int currentPC;
    public Instruction output;

    public Decode(Registers registers) {
        this.registers = registers;
    }
    
    public void decode() {
        // Do not run if instruction isn't fetched
        if(input == 0) return;
        
        Type type = Opcode.getInstructionType(Opcode.getOpcode(input));

        output = switch (type){
            case B_TYPE -> BInstruction.decode(input, currentPC, registers);
            case I_TYPE -> IInstruction.decode(input, currentPC, registers);
            case J_TYPE -> JInstruction.decode(input, currentPC, registers);
            case R_TYPE -> RInstruction.decode(input, currentPC, registers);
            case S_TYPE -> SInstruction.decode(input, currentPC, registers);
            case U_TYPE -> UInstruction.decode(input, currentPC, registers);
        };
    }

    @Override
    public String toString() {
        return String.format("""
                Decode:
                    Current PC Input: 0x%s
                    Input: 0x%s
                    Output: %s""", Integer.toHexString(currentPC), Integer.toHexString(input), output);
    }
}
