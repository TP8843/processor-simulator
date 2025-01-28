package org.example.processor;

public class MemoryAccessor {
    public Instruction input;
    private Memory memory;
    private RegisterWriteBack registerWriteBack;
    
    public int storeValue;
    
    MemoryAccessor(Memory memory) {
        this.memory = memory;
    }
    
    public void processInstruction() {
        Instruction output = input;
        
        if (input.opcode == Instruction.Opcode.STOR) {
            memory.storeValue(input.output, storeValue);
            output.writeBackAddress = Registers.Address.$0;
        }
        
        if (input.opcode == Instruction.Opcode.LOAD) {
            output.output = memory.getValue(input.output);
        }
        
        registerWriteBack.input = output;
    }
}
