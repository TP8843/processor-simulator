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
        byte writeBackAddress = input.destination;
        int output = input.aluOutput;
        
        if (input.opcode == Instruction.Opcode.STOR) {
            memory.storeValue(input.aluOutput, input.memoryStoreValue);
            writeBackAddress = 0;
        }
        
        if (input.opcode == Instruction.Opcode.LOAD) {
            output = memory.getValue(input.aluOutput);
        }

        registerWriteBack.input = new Instruction(
                input.opcode, input.aluInput1, input.aluInput2, writeBackAddress, output, input.memoryStoreValue, input.compareUnitOutput 
        );
    }

    @Override
    public String toString() {
        return String.format("""
                Memory Accessor:
                    Input: %s""", input);
    }
}
