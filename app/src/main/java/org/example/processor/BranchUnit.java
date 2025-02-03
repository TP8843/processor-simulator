package org.example.processor;

public class BranchUnit {
    private ProgramStore programStore;
    
    public DecodedInstruction compareUnitInput;
    public DecodedInstruction aluInput;
    
    public BranchUnit() {}
    
    public void setProgramStore(ProgramStore programStore) {
        this.programStore = programStore;
    }
    
    public void process() {
        if ((compareUnitInput.opcode == DecodedInstruction.Opcode.SLT || 
             compareUnitInput.opcode == DecodedInstruction.Opcode.SLTI) &&
             compareUnitInput.aluInput1 < compareUnitInput.aluInput2) {
                programStore.updatePC(programStore.getCurrentPC() + aluInput.aluOutput);
        }
    }

    @Override
    public String toString() {
        return String.format("""
                Program Count Updater:
                    Instruction from compare unit: %s
                    Instruction from ALU: %s""", compareUnitInput, aluInput);
        
    }
}
