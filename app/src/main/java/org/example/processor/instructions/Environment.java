package org.example.processor.instructions;

import org.example.processor.commit.ROB;
import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.IInstructions.EInstructions.EBreakInstruction;
import org.example.processor.instructions.IInstructions.EInstructions.ECallInstruction;
import org.example.processor.instructions.IInstructions.IInstruction;

public abstract class Environment implements Instruction{
    private final Opcode opcode;

    private final int PC;

    public Environment(Opcode opcode, int PC) {
        this.opcode = opcode;
        this.PC = PC;
    }

    @Override
    public Opcode getOpcode() {
        return opcode;
    }

    @Override
    public int getPC() {
        return PC;
    }

    @Override
    public boolean canBranch() {
        return false;
    }

    @Override
    public boolean hasData() {
        return true;
    }

    @Override
    public void initOperands(ROB rob) {}

    @Override
    public void getDataIfAvailable() {}

    @Override
    public EU getEU() {
        return EU.NONE;
    }

    static public Environment decode(int instruction, int PC) {
        Opcode opcode = Opcode.getOpcode(instruction);

        return switch (IInstruction.decodeImmediate(instruction)) {
            case 0x0 -> new ECallInstruction(opcode, PC);
            case 0x1 -> new EBreakInstruction(opcode, PC);
            default -> throw new IllegalArgumentException("invalid opcode for Environment instruction " + instruction);
        };
    }
}
