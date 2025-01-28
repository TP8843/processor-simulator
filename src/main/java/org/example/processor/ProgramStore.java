package org.example.processor;

public class ProgramStore {
    private String[] program;
    private final Decode decode;
    
    private boolean halted = false;
    
    public int currentInstruction;
    
    ProgramStore(Decode decode, String file){
        this.currentInstruction = 0;
        this.decode = decode;
        
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
