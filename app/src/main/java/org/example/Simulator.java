package org.example;

import org.example.processor.*;

public class Simulator {
    public final Memory memory;
    public final Registers registers;
    public final InstructionFetch instructionFetch;
    public final Decode decode;
    public final Alu alu;
    public final CompareUnit compareUnit;
    public final BranchUnit branchUnit;
    public final MemoryAccessUnit memoryAccessUnit;
    public final WriteBackUnit writeBackUnit;
    
    /// Whether branch store currently occuring
    private boolean branchStall = false;

    public Simulator(Memory memory,
                     Registers registers,
                     InstructionFetch instructionFetch,
                     Decode decode,
                     Alu alu,
                     CompareUnit compareUnit,
                     BranchUnit branchUnit,
                     MemoryAccessUnit memoryAccessUnit,
                     WriteBackUnit writeBackUnit) {
        this.memory = memory;
        this.registers = registers;
        this.instructionFetch = instructionFetch;
        this.decode = decode;
        this.alu = alu;
        this.compareUnit = compareUnit;
        this.branchUnit = branchUnit;
        this.memoryAccessUnit = memoryAccessUnit;
        this.writeBackUnit = writeBackUnit;
    }
    
    public boolean getBranchStall() {
        return branchStall;
    }

    public void runCycle() {
        writeBackUnit.writeBack();
        if (writeBackUnit.input != null)
            System.out.println(String.format("Written back %s", Integer.toHexString(writeBackUnit.input.getPC())));

        memoryAccessUnit.process();
        writeBackUnit.input = memoryAccessUnit.output;
        
        branchUnit.updatePC();
        // Once branch unit has updated the PC, instruction fetch can fetch again :D
        if(branchUnit.aluInput != null && branchUnit.aluInput.canBranch()) {
            branchStall = false;
        }

        alu.execute();
        compareUnit.execute();
        branchUnit.compareInput = compareUnit.output;
        branchUnit.aluInput = alu.output;
        memoryAccessUnit.input = alu.output;

        decode.decode();
        alu.input = decode.output;
        compareUnit.input = decode.output;

        // Stop fetching of instructions until branching instruction finishes
        if(branchStall == false && decode.output != null && decode.output.canBranch()) {
            branchStall = true;
            instructionFetch.output = 0;
        }

        if (branchStall == false && decode.isReady()) {
            instructionFetch.process();
        }

        decode.input = instructionFetch.output;
        decode.currentPC = instructionFetch.getPC() - 4;
    }

    static public Simulator createSimulator(String fileName) {
        Memory memory = new Memory();
        Registers registers = new Registers();
        InstructionFetch instructionFetch = new InstructionFetch(memory, 8);
        Decode decode = new Decode(registers);
        Alu alu = new Alu();
        CompareUnit compareUnit = new CompareUnit();
        BranchUnit branchUnit = new BranchUnit(instructionFetch);
        MemoryAccessUnit memoryAccessUnit = new MemoryAccessUnit(memory);
        WriteBackUnit writeBackUnit = new WriteBackUnit(registers);

        memory.loadProgram(fileName, 8);

        return new Simulator(
                memory,
                registers,
                instructionFetch,
                decode,
                alu,
                compareUnit,
                branchUnit,
                memoryAccessUnit,
                writeBackUnit
        );
    }
}
