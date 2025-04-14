package org.example.processor.instructions.IInstructions.LoadInstructions;

import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.SInstructions.SBInstruction;
import org.example.processor.instructions.SInstructions.SHWInstruction;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.SInstructions.SWInstruction;

import java.util.HashMap;
import java.util.Map;

public class LoadInstruction extends IInstruction {
    /// Whether an address has been calculated for loading
    private boolean hasAddress = false;

    /// Address to load data from
    private int address;

    /// Map to store all sources of data for instruction
    private final Map<Integer, SInstruction> sources = new HashMap<>();

    /// What parts of memory to mask on the memory load
    private int memoryMask;

    /// Whether the instruction is ready for a normal memory load
    private boolean hasData = false;

    public LoadInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        super(opcode, PC, rs1, imm, rd);
    }

    /// Adds a store instruction as a source for the load
    public void addSource(SInstruction s) {
        sources.put(s.getAddress(), s);
    }

    /// Get any new available data from the memory sources
    @Override
    public void getDataIfAvailable(){
        for (Map.Entry<Integer, SInstruction> entry : sources.entrySet()) {
            if(!entry.getValue().isReady()) continue;

            sources.remove(entry.getKey());

            int offset = entry.getKey() - address;
            switch (entry.getValue()) {
                case SWInstruction i -> {
                    this.result = i.getValue();
                    this.memoryMask |= 0xffff;
                }
                case SHWInstruction i -> {
                    this.result = (i.getValue() & 0xffff) << ((2 - offset) * 8);
                    this.memoryMask |= 0xffff << ((2 - offset) * 8);
                }
                case SBInstruction i -> {
                    this.result = (i.getValue() & 0xff) << ((3 - offset) * 8);
                    this.memoryMask |= 0xff << ((3 - offset) * 8);
                }
                default -> {}
            }
        }

        if(sources.isEmpty()) this.hasData = true;
    }

    /// Get the current memory mask
    public int getMemoryMask() {
        return memoryMask;
    }

    /// Whether the instruction is ready for a load from memory
    @Override
    public boolean hasData() {
        return hasData;
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
