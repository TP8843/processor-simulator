package org.example.processor.buffers;

import org.example.processor.*;
import org.example.processor.instructions.Branch;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.MemoryWrite;
import org.example.processor.instructions.RegisterWrite;

import java.util.Arrays;

public class ROB implements Flushable {
    /// Size of the reorder buffer
    public static final int SIZE = 128;

    /// Current head of ROB
    private int head = 0;

    /// Current tail of ROB
    private int tail = 0;

    /// Instruction just commited (for debugging)
    private Instruction previous;

    /// Backing array for ROB
    private final Instruction[] instructions = new Instruction[SIZE];

    /// Branch Unit (to handle mispredictions)
    private final BranchUnit branchUnit;

    /// Allows for writing to memory on commit
    private final MemoryWriteUnit memoryUnit;

    /// Allows for writing to registers on commit
    private final Registers registers;

    public ROB(BranchUnit branchUnit, MemoryWriteUnit memoryUnit, Registers registers) {
        this.branchUnit = branchUnit;
        this.memoryUnit = memoryUnit;
        this.registers = registers;
    }

    /// Gets instruction just commited by ROB
    public Instruction getPrevious() {
        return previous;
    }

    /// Whether there is space to add an item to the ROB
    public boolean hasSpace() {
        return tail + 1 != head;
    }

    /// Adds an instruction to the ROB if there is space
    public void add(Instruction instruction) {
        if(isFull()) return;

        instructions[tail] = instruction;
        tail = (tail + 1) % SIZE;
    }

    /// Process instruction at the head of the ROB. True if instruction commited
    public boolean processHead() {
        if(isEmpty()) return false;

        Instruction instruction = instructions[head];

        // Only process the head if the instruction is ready
        if(!instruction.isReady()) return false;

        previous = instruction;
        head = (head + 1) % SIZE;

        handleInstruction(instruction);

        return true;
    }

    /// Commit an instruction
    private void handleInstruction(Instruction instruction){
        switch (instruction){
            case Branch i -> {
                // If it has its result, and we shouldn't have branched, flush the pipeline
                if(i.hasResult() && !i.getResult()){
                    flush();
                    branchUnit.branchMispredict(i);
                }
            }

            case RegisterWrite i -> registers.setRegister(i.getDestination(), i.getResult());

            case MemoryWrite i -> memoryUnit.writeMemory(i);

            default -> {}
        }
    }

    /// Whether the ROB is full
    public boolean isFull(){
        return (tail - head == SIZE - 1) || (tail - head == -1);
    }

    /// Whether the ROB is empty
    public boolean isEmpty(){
        return tail == head;
    }

    @Override
    public void flush(){
        // Don't actually have to remove data as resetting the pointers is enough
        head = 0;
        tail = 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("ROB - :\n");
        builder.append("    head: " + head + "\n");
        builder.append("    tail: " + tail + "\n");
        builder.append("    previous: " + previous + "\n");
        builder.append("    instructions:\n");

        int i = head;
        int j = 0;

        while (i != tail) {
            builder.append(String.format("Instruction %d: %s\n", j, instructions[i]));
            i = (i + 1) % SIZE;
            j += 1;
        }

        return builder.toString();
    }
}
