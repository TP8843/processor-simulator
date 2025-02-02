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
        byte writeBackAddress = input.writeBackAddress;
        int output = input.output;
        
        if (input.opcode == Instruction.Opcode.STOR) {
            memory.storeValue(input.output, input.storeValue);
            writeBackAddress = 0;
        }
        
        if (input.opcode == Instruction.Opcode.LOAD) {
            output = memory.getValue(input.output);
        }

        registerWriteBack.input = new Instruction(
                input.opcode, input.operand1, input.operand2, writeBackAddress, output, input.storeValue, input.branchCheck 
        );
    }

    @Override
    public String toString() {
        return String.format("""
                Memory Accessor:
                    Input: %s""", input);
    }
}
