package org.example.processor;

public class ProgramCountUpdater {
    private int currentPC;
    public Instruction compareInput;
    public Instruction aluInput;
    
    public ProgramCountUpdater() {
        this.currentPC = 0;
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
        
        currentPC = newPC;
    }
    
    public int getCurrentPC() {
        return currentPC;
    }

    @Override
    public String toString() {
        return String.format("""
                Program Count Updater:
                    Current PC: %s
                    Instruction from compare unit: %s
                    Instruction from ALU: %s""", getCurrentPC(), compareInput, aluInput);
        
    }
}
