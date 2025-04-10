package org.example.processor.instructions.IInstructions.EInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.Environment;
import org.example.processor.instructions.IInstructions.IInstruction;

public class EBreakInstruction extends Environment {
    public EBreakInstruction(Opcode opcode, int PC) {
        super(opcode, PC);
    }
}
