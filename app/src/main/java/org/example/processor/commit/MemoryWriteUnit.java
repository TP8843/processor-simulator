package org.example.processor.commit;

import org.example.processor.data.Memory;
import org.example.processor.instructions.MemoryWrite;
import org.example.processor.instructions.SInstructions.SBInstruction;
import org.example.processor.instructions.SInstructions.SHWInstruction;
import org.example.processor.instructions.SInstructions.SWInstruction;

public class MemoryWriteUnit {
    private final Memory memory;

    public MemoryWriteUnit(Memory memory) {
        this.memory = memory;
    }

    public void writeMemory(MemoryWrite instruction) {
        
        switch (instruction) {
            case SBInstruction i -> memory.storeByte(i.getAddress(), i.rs2.getData());
            case SHWInstruction i -> memory.storeHalfWord(i.getAddress(), i.rs2.getData());
            case SWInstruction i -> memory.storeWord(i.getAddress(), i.rs2.getData());
            
            default -> {}
        }
    }
}
