package org.example;

import org.example.processor.*;
import org.example.processor.instructions.*;

import java.util.Scanner;

public class Simulator {
    private final Memory memory;
    private final Registers registers;
    private final InstructionFetch instructionFetch;
    private final Decode decode;
    private final Alu alu;
    private final CompareUnit compareUnit;
    private final BranchUnit branchUnit;
    private final MemoryAccessUnit memoryAccessUnit;
    private final WriteBackUnit writeBackUnit;

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

    public void runSimulator() {
//        Scanner scanner = new Scanner(System.in);

        while (!alu.isHalted()) {
            runCycle();
        }
        
        System.out.println(registers);
        System.out.println("Final value: " + memory);
    }

    private void runCycle() {
        switch (stage){
            case 0 -> {
                instructionFetch.process();
                decode.input = instructionFetch.output;
                decode.currentPC = instructionFetch.getPC() - 4;
                    System.out.println("Current PC: " + Integer.toHexString(decode.currentPC) + " " + String.format("%32s", Integer.toBinaryString(instructionFetch.output)).replace(' ', '0'));
            }
            case 1 -> {
                decode.decode();
                alu.input = decode.output;
                compareUnit.input = decode.output;
            }
            case 2 -> {
                alu.execute();
                compareUnit.execute();
                branchUnit.compareInput = compareUnit.output;
                branchUnit.aluInput = alu.output;
                memoryAccessUnit.input = alu.output;

                System.out.println(alu.output);
            }
            case 3 -> {
                memoryAccessUnit.process();
                branchUnit.updatePC();
                writeBackUnit.input = memoryAccessUnit.output;
            }
            case 4 -> {
                writeBackUnit.writeBack();
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
