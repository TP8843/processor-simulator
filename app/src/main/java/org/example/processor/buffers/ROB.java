package org.example.processor.buffers;

import org.example.processor.instructions.Instruction;

public class ROB {
    /// Size of the reorder buffer
    public static final int SIZE = 128;

    /// Current head of ROB
    private int head = 0;

    /// Current tail of ROB
    private int tail = 0;

    /// Backing array for ROB
    private Instruction[] instructions = new Instruction[SIZE];

    /// Whether there is space to add an item to the ROB
    public boolean hasSpace() {
        return tail + 1 != head;
    }

    /// Adds an instruction to the ROB if there is space
    public void add(Instruction instruction) {
        if(tail + 1 == head) {
            return;
        }

        instructions[tail] = instruction;
        tail += 1;
    }

    /// Process instruction at the head of the ROB
    public void processHead() {
        if(head == tail) return;

        // TODO: Actually process the instruction
        instructions[head] = null;
        head += 1;
    }
}
