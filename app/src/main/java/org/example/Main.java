package org.example;

import org.example.processor.*;

public class Main {
    private static void startSimulator(String path){
        System.out.println("Running program from " + path);

        Registers registers = new Registers();
        Memory memory = new Memory();

        RegisterWriteBack registerWriteBack = new RegisterWriteBack(registers);
        MemoryAccessor memoryAccessor = new MemoryAccessor(memory, registerWriteBack);
        BranchUnit branchUnit = new BranchUnit();
        CompareUnit compareUnit = new CompareUnit(branchUnit);
        Alu alu = new Alu(memoryAccessor, branchUnit);

        Decode decode = new Decode(registers, alu, compareUnit);
        ProgramStore programStore = new ProgramStore(decode, path);
        
        branchUnit.setProgramStore(programStore);

        Simulator simulator = new Simulator(
                programStore,
                decode,
                compareUnit,
                alu,
                memoryAccessor,
                memory,
                registers,
                branchUnit,
                registerWriteBack
        );

        simulator.run();
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must include a file path for an assembly file");
            return;
        }
        
        startSimulator(args[0]);
    }
}