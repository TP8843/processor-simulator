package org.example.processor;

public class MemoryAccessor {
    public Instruction input;
    private final Memory memory;
    private final RegisterWriteBack registerWriteBack;
    
    public int storeValue;
    
    public MemoryAccessor(Memory memory, RegisterWriteBack registerWriteBack) {
        this.memory = memory;
        this.registerWriteBack = registerWriteBack;
    }
    
    public void processInstruction() {
        System.out.println("Instruction state at start of memory accessor: " + input);


        Instruction output = input;
        
        if (input.opcode == Instruction.Opcode.STOR) {
            memory.storeValue(input.output, input.storeValue);
            output.writeBackAddress = 0;
        }
        
        if (input.opcode == Instruction.Opcode.LOAD) {
            output.output = memory.getValue(input.output);
        }
        
        System.out.println("Instruction state in memory accessor: " + output);
        
        registerWriteBack.input = output;
    }
}
