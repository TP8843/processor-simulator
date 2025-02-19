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

    private int stage = 0;

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
    
    public int getStage() {
        return stage;
    }

    public void runCycle() {
        switch (stage){
            case 0 -> {
                instructionFetch.process();
                decode.input = instructionFetch.output;
                decode.currentPC = instructionFetch.getPC() - 4;
            }
            case 1 -> {
                decode.decode();
                alu.input = decode.output;
                compareUnit.input = decode.output;
                
                // Stop fetching of instructions until branching instruction finishes
                if(decode.output.canBranch()) instructionFetch.fetching = false;
            }
            case 2 -> {
                alu.execute();
                compareUnit.execute();
                branchUnit.compareInput = compareUnit.output;
                branchUnit.aluInput = alu.output;
                memoryAccessUnit.input = alu.output;
            }
            case 3 -> {
                memoryAccessUnit.process();
                writeBackUnit.input = memoryAccessUnit.output;

                branchUnit.updatePC();
                // Once branch unit has updated the PC, instruction fetch can fetch again :D
                instructionFetch.fetching = true;
            }
            case 4 -> {
                writeBackUnit.writeBack();
                
                System.out.println("Finished processing instruction " + Integer.toHexString(instructionFetch.getPC() - 4));
            }
        }
        stage = (stage + 1) % 5;
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
