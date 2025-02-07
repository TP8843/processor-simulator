package org.example;

import org.example.processor.*;
import org.example.processor.instructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.SInstruction;
import org.example.processor.instructions.UInstruction;

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

        while (!instructionFetch.isHalted()) {
            runCycle();
        }
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
                if(alu.output.getType() == Instruction.Type.S_TYPE || alu.output.getType() == Instruction.Type.U_TYPE)
                    System.out.println("Alu Output: " + alu.output);
                if(alu.output.getType() == Instruction.Type.U_TYPE)
                    System.out.println("Alu Output U Type: " + Integer.toBinaryString(((UInstruction)alu.output).aluResult));

                // Program is attempting to send the main return back to the processor. Stop execution
                if (alu.output.getType() == Instruction.Type.S_TYPE && ((SInstruction)alu.output).aluResult == 0x3000008) {
                    return;
                }
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
        InstructionFetch instructionFetch = new InstructionFetch(memory, 0);
        Decode decode = new Decode(registers);
        Alu alu = new Alu();
        CompareUnit compareUnit = new CompareUnit();
        BranchUnit branchUnit = new BranchUnit(instructionFetch);
        MemoryAccessUnit memoryAccessUnit = new MemoryAccessUnit(memory);
        WriteBackUnit writeBackUnit = new WriteBackUnit(registers);

        memory.loadProgram(fileName, 0);

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
