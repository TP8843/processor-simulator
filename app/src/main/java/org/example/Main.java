package org.example;

import org.example.processor.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must include a file path for an assembly file");
            return;
        }
        
        System.out.println("Running program from " + args[0]);

        Registers registers = new Registers();
        Memory memory = new Memory();

        RegisterWriteBack registerWriteBack = new RegisterWriteBack(registers);
        MemoryAccessor memoryAccessor = new MemoryAccessor(memory, registerWriteBack);
        ProgramCountUpdater programCountUpdater = new ProgramCountUpdater();
        CompareUnit compareUnit = new CompareUnit(programCountUpdater);
        Alu alu = new Alu(memoryAccessor);

        Decode decode = new Decode(registers, alu, compareUnit);
        ProgramStore programStore = new ProgramStore(decode, programCountUpdater, args[0]);

        Simulator simulator = new Simulator(
                programStore,
                decode,
                compareUnit,
                alu,
                memoryAccessor,
                memory,
                registers,
                programCountUpdater,
                registerWriteBack
        );

        simulator.run();
    }
}