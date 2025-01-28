package org.example;

import org.example.processor.*;

public class Simulator {
    private final ProgramStore programStore;
    private final Decode decode;
    private final CompareUnit compareUnit;
    private final Alu alu;
    private final MemoryAccessor memoryAccessor;
    private final ProgramCountUpdater programCountUpdater;
    private final RegisterWriteBack registerWriteBack;
    
    private int cycles;
    
    Simulator(ProgramStore programStore, 
              Decode decode,
              CompareUnit compareUnit,
              Alu alu,
              MemoryAccessor memoryAccessor,
              ProgramCountUpdater programCountUpdater,
              RegisterWriteBack registerWriteBack) {
        this.programStore = programStore;
        this.decode = decode;
        this.compareUnit = compareUnit;
        this.alu = alu;
        this.memoryAccessor = memoryAccessor;
        this.programCountUpdater = programCountUpdater;
        this.registerWriteBack = registerWriteBack;
        cycles = 0;
    }
    
    public void run() {
        int currentStage = 0;

        while (true) {
            switch (currentStage) {
                case 0 -> {
                    if (programStore.getHalted()) return;
                    
                    programStore.getInstruction();
                    cycles += 1;
                }
                case 1 -> {
                    decode.decode();
                    cycles += 1;
                }
                case 2 -> {
                    alu.execute();
                    compareUnit.process();
                    cycles += 1;
                }
                case 3 -> {
                    memoryAccessor.processInstruction();
                    programCountUpdater.process();
                    cycles += 1;
                }
                case 4 -> {
                    registerWriteBack.processWriteBack();
                    cycles += 1;
                }
            }
            
            currentStage = (currentStage + 1) % 5;
        }
    }
}
