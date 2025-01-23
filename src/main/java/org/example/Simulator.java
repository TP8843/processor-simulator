package org.example;

import org.example.processor.*;

public class Simulator {
    private Decode decode;
    private Alu alu;
    private ProgramStore programStore;
    private DataStore registers;
    private DataStore memory;
    
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
            switch (currentStage) {
                case 0: return FETCH;
                case 1: return DECODE;
                case 2: return EXECUTE;
                case 3: return MEMORY;
                case 4: return WRITE_BACK;
                default: return null;
            }
        }
        
        Stage(){
            currentStage = 0;
        }
    }
    
    Simulator(Decode decode, Alu alu, ProgramStore programStore, DataStore registers, DataStore memory) {
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
            if (Instruction.Opcode.usesAlu(instruction.opcode)) {
                int output = alu.execute(instruction);
            }
        }
    }
}
