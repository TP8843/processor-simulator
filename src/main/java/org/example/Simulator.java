package org.example;

import org.example.processor.*;

public class Simulator {
    private Registers registers;
    private Memory memory;
    
    private ProgramStore programStore;
    private Decode decode;
    private CompareUnit compareUnit;
    private Alu alu;
    private MemoryAccessor memoryAccessor;
    private ProgramCountUpdater programCountUpdater;
    private RegisterWriteBack registerWriteBack;
    
    private boolean halt;
    
    private int cycles;
    
    private enum Stage {
        FETCH,
        DECODE,
        EXECUTE,
        MEMORY,
        WRITE_BACK;
        
        int currentStage;
        
        public void nextStage() {
            currentStage += currentStage % 5;
        }
        
        public Stage getStage() {
            return switch (currentStage) {
                case 0 -> FETCH;
                case 1 -> DECODE;
                case 2 -> EXECUTE;
                case 3 -> MEMORY;
                case 4 -> WRITE_BACK;
                default -> null;
            };
        }
        
        Stage(){
            currentStage = 0;
        }
    }
    
    Simulator(Registers registers, 
              Memory memory, 
              ProgramStore programStore, 
              Decode decode,
              CompareUnit compareUnit,
              Alu alu,
              MemoryAccessor memoryAccessor,
              ProgramCountUpdater programCountUpdater,
              RegisterWriteBack registerWriteBack) {
        this.registers = registers;
        this.memory = memory;
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
        Stage currentStage = Stage.FETCH;

        while (true) {
            switch (currentStage) {
                case FETCH -> {
                    if (programStore.getHalted()) return;
                    
                    programStore.getInstruction();
                    cycles += 1;
                }
                case DECODE -> {
                    decode.decode();
                    cycles += 1;
                }
                case EXECUTE -> {
                    alu.execute();
                    cycles += 1;
                }
                case MEMORY -> {
                    memoryAccessor.processInstruction();
                    programCountUpdater.process();
                    cycles += 1;
                }
                case WRITE_BACK -> {
                    registerWriteBack.processWriteBack();
                    cycles += 1;
                }
            }
        }
    }
}
