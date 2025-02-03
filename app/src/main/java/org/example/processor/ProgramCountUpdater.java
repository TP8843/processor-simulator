package org.example.processor;

public class ProgramCountUpdater {
    private int currentPC;
    public Instruction compareUnitInput;
    public Instruction aluInput;
    
    public ProgramCountUpdater() {
        this.currentPC = 0;
    }
    
    public void process() {
        int newPC = currentPC;
        if ((compareUnitInput.opcode == Instruction.Opcode.SLT || 
             compareUnitInput.opcode == Instruction.Opcode.SLTI) &&
             compareUnitInput.aluInput1 < compareUnitInput.aluInput2) {
                newPC += aluInput.aluOutput;
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
                    Instruction from ALU: %s""", getCurrentPC(), compareUnitInput, aluInput);
        
    }
}
