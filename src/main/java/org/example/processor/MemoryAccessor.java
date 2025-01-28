package org.example.processor;

public class MemoryAccessor {
    public Instruction input;
    private final Memory memory;
    private final RegisterWriteBack registerWriteBack;
    
    public int storeValue;
    
    MemoryAccessor(Memory memory, RegisterWriteBack registerWriteBack) {
        this.memory = memory;
        this.registerWriteBack = registerWriteBack;
    }
    
    public void processInstruction() {
        Instruction output = input;
        
        if (input.opcode == Instruction.Opcode.STOR) {
            memory.storeValue(input.output, storeValue);
            output.writeBackAddress = 0;
        }
        
        if (input.opcode == Instruction.Opcode.LOAD) {
            output.output = memory.getValue(input.output);
        }
        
        registerWriteBack.input = output;
    }
}
