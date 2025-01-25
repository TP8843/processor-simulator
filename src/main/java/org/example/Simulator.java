package org.example;

import org.example.processor.*;

public class Simulator {
    private Decode decode;
    private Alu alu;
    private ProgramStore programStore;
    private Registers registers;
    private Memory memory;
    
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
    
    Simulator(Decode decode, Alu alu, ProgramStore programStore, Memory registers, Memory memory) {
        this.decode = decode;
        this.alu = alu;
        this.programStore = programStore;
        this.registers = registers;
        this.memory = memory;
        cycles = 0;
    }
    
    public void run() {
        while (!programStore.isEndReached()) {
            // Fetch
            String currentInstruction = programStore.getNextInstruction();
            cycles += 1;
            
            // Decode
            Instruction instruction = decode.decode(currentInstruction);
            cycles += 1;
            
            // Execute
            int output = alu.execute(instruction);
        }
    }
}
