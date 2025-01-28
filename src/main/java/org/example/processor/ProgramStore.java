package org.example.processor;

import org.example.Simulator;

public class ProgramStore {
    private String[] program;
    private Decode decode;
    private Simulator simulator;
    
    private boolean halted = false;
    
    public int currentInstruction;
    
    ProgramStore(Decode decode, Simulator simulator, String file){
        this.currentInstruction = 0;
        
        // TODO: Load program from file and split on newline
    }
    
    public boolean getHalted() {
        return halted;
    }
    
    public void updatePC(int position) {
        currentInstruction = position;
    }
    
    public void getInstruction() {
        if(currentInstruction >= program.length) {
            halted = true;
            return;
        }
        
        decode.input = program[currentInstruction - 1];
    }
}
