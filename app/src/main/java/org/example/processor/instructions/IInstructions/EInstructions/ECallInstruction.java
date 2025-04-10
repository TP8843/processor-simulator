package org.example.processor.instructions.IInstructions.EInstructions;

import org.example.processor.instructions.Environment;

public class ECallInstruction extends Environment {
    public ECallInstruction(Opcode opcode, int PC) {
        super(opcode, PC);
    }
}
