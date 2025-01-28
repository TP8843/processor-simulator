package org.example.processor;

public class ProgramCountUpdater {
    public int currentPC;
    public Instruction compareInput;
    public Instruction aluInput;
    
    private ProgramStore programStore;
    
    ProgramCountUpdater(ProgramStore programStore) {
        this.programStore = programStore;
    }
    
    public void process() {
        int newPC = currentPC;
        if ((compareInput.opcode == Instruction.Opcode.SLT || 
             compareInput.opcode == Instruction.Opcode.SLTI) &&
             compareInput.operand1 < compareInput.operand2) {
                newPC += aluInput.output;
        } else {
            newPC += 1;
        }
        
        programStore.updatePC(newPC);
    }
}
