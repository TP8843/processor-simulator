package org.example.processor.instructions.IInstructions.LoadInstructions;

import org.example.processor.commit.ROB;
import org.example.processor.executionUnits.EU;
import org.example.processor.instructions.IInstructions.IInstruction;
import org.example.processor.instructions.SInstructions.SBInstruction;
import org.example.processor.instructions.SInstructions.SHWInstruction;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.SInstructions.SWInstruction;

import java.util.*;

public class LoadInstruction extends IInstruction {
    /// Whether an address has been calculated for loading
    private boolean hasAddress = false;

    /// Address to load data from
    private int address;

    /// True when all initial sources have addresses (and current instruction has address)
    private boolean initialSourcesFinalised = true;

    private final List<SInstruction> initialSources = new LinkedList<>();

    /// Map to store all sources of data for instruction
    private final List<SInstruction> sources = new LinkedList<>();

    private final List<Integer> masks = new LinkedList<>();

    /// What parts of memory to mask on the memory load
    private int memoryMask;

    /// Whether the instruction is ready for a normal memory load
    private boolean hasData = false;

    public LoadInstruction(Opcode opcode, int PC, byte rs1, int imm, byte rd) {
        super(opcode, PC, rs1, imm, rd);
    }

    /// Add initial sources before filtering and checking
    public void addInitialSource(SInstruction s){
        initialSources.add(s);
        this.initialSourcesFinalised = false;
    }

    /// Adds a store instruction as a source for the load
    private void addSource(SInstruction s) {
        if(memoryMask == -1) return;

        sources.add(s);

        int offset = s.getAddress() - getAddress();
        int initialMask = switch (s){
            case SWInstruction _ -> 0xffffffff;
            case SHWInstruction _ -> 0xffff << (offset * 8);
            case SBInstruction _ -> 0xff << (offset * 8);
            default -> 0x0;
        };

        // Get the mask for the specific instruction. Gives more recent instructions priority
        var uniqueMask = (memoryMask ^ initialMask) & initialMask;

        masks.add(uniqueMask);

        memoryMask |= initialMask;
    }

    /// Get data for register
    @Override
    public void getDataIfAvailable(){
        rs1.getDataWhenAvailable();
    }

    /// Get any new available data from the memory sources
    public void getMemoryDataIfAvailable(){
        if(!initialSourcesFinalised) {
            // Start with initial sources until all have addresses
            for(SInstruction s : initialSources) {
                // Return if a source does not yet have its address
                if(!s.hasAddress()) return;
            }

            // Only run once all instructions have addresses
            this.initialSourcesFinalised = true;

            byte bytes = switch (this) {
                case LHWInstruction _, LHWUInstruction _ -> 2;
                case LBInstruction _, LBUInstruction _ -> 1;
                default -> 4;
            };

            for(SInstruction s : initialSources) {
                // If instruction is a load instruction in the correct range of addresses
                if(getAddress() >= s.getAddress() &&
                   getAddress() < s.getAddress() + bytes){
                    addSource(s);
                }
            }

            initialSources.clear();
        }

        // Else for when finalised sources have been initialised
        else {
            // Reserved so items can be removed without affecting iteration
            for (int j = sources.size() - 1; j >= 0; j--) {
                SInstruction entry = sources.get(j);
                int mask = masks.get(j);

                if(!entry.isReady()) continue;

                sources.remove(j);
                masks.remove(j);

                int offset = entry.getAddress() - address;
                this.result |= ((entry.getValue() << offset * 8) & mask);
            }

            if(sources.isEmpty()) this.hasData = true;
        }
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
                        Initial Sources: %s
                        Initial Sources Finalised: %s
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
                initialSources,
                initialSourcesFinalised ? "True" : "False",
                sources,
                imm,
                rd,
                hasResult() ? "True" : "False",
                result);
    }
}
