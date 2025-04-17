package org.example.processor.instructions.IInstructions.LoadInstructions;

import org.example.processor.commit.ROB;
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

    /// Get data for register
    @Override
    public void getDataIfAvailable(){
        rs1.getDataWhenAvailable();
    }

    /// Get any new available data from the memory sources
    public void getMemoryDataIfAvailable(){
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
                    this.result = (i.getValue() & 0xffff) << (offset * 8);
                    this.memoryMask |= 0xffff << (offset * 8);
                }
                case SBInstruction i -> {
                    this.result = (i.getValue() & 0xff) << (offset * 8);
                    this.memoryMask |= 0xff << (offset * 8);
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
        return rs1.hasData();
    }

    public boolean hasMemoryData() {
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

    @Override
    public String toString() {
        return String.format("""
                I Type Instruction:
                        Opcode: %s
                        PC: %s
                        Is Ready: %s
                        RS1: %s
                        Has Data: %s
                        Memory Mask: %s
                        Sources: %s
                        IMM: %s
                        RD: %s
                        Has Result: %s
                        Result:  %s""",
                getOpcode(),
                getPC(),
                isReady() ? "True" : "False",
                rs1,
                hasData() ? "True" : "False",
                memoryMask,
                sources,
                imm,
                rd,
                hasResult() ? "True" : "False",
                result);
    }
}
