package org.example.processor;

public class ProgramStore {
    private String[] program;
    
    private int currentInstruction;
    private boolean endReached;
    
    ProgramStore(String file){
        this.currentInstruction = 0;
        this.endReached = false;
        
        // TODO: Load program from file and split on newline
    }
    
    public boolean isEndReached() { return endReached; }
    
    public void jump(int position) {
        currentInstruction = position;
    }
    
    public String getNextInstruction() {
        if(endReached) return null;
        
        currentInstruction++;
        
        if (currentInstruction >= program.length) endReached = true;
        
        return program[currentInstruction - 1];
    }
}
