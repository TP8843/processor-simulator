package org.example.processor.instructions.IInstructions.LoadInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.IInstructions.IInstruction;

public class LoadInstruction extends IInstruction {
    /// Whether an address has been calculated for loading
    private boolean hasAddress = false;

    /// Address to load data from
    private int address;

    public LoadInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        super(opcode, PC, rs1, imm, rd);
    }

    /// Add address to instruction
    public void addAddress(int address) {
        this.hasAddress = true;
        this.address = address;
    }

    /// Whether an address has been calculated for the instruction
    public boolean hasAddress() {
        return hasAddress;
    }

    /// The address to load data from
    public int getAddress() {
        return address;
    }

    @Override
    public EU getEU() {
        return EU.AGU;
    }
}
