package org.example.processor.commit;

import org.example.processor.branch.BranchUnit;
import org.example.processor.buffers.CircularQueue;
import org.example.processor.buffers.Flushable;
import org.example.processor.data.Registers;
import org.example.processor.instructions.*;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.SInstructions.SInstruction;

import java.util.Iterator;
import java.util.Optional;

public class ROB implements Flushable {
    /// Size of the reorder buffer
    public final int size;

    /// Instruction just commited (for debugging)
    private Instruction previous;

    /// Backing queue for ROB
    private final CircularQueue<Instruction> queue;

    /// Branch Unit (to handle mispredictions)
    private final BranchUnit branchUnit;

    /// Allows for writing to memory on commit
    private final MemoryWriteUnit memoryUnit;

    /// Allows for writing to registers on commit
    private final Registers registers;

    private boolean halted;

    public ROB(BranchUnit branchUnit, MemoryWriteUnit memoryUnit, Registers registers) {
        this(branchUnit, memoryUnit, registers, 128);
    }

    public ROB(BranchUnit branchUnit, MemoryWriteUnit memoryUnit, Registers registers, int size) {
        this.branchUnit = branchUnit;
        this.memoryUnit = memoryUnit;
        this.registers = registers;
        this.size = size;
        this.queue = new CircularQueue<>(size);
    }

    /// Gets instruction just commited by ROB
    public Instruction getPrevious() {
        return previous;
    }

    /// Whether the program has halted
    public boolean getHalted() {
        return halted;
    }

    /// Adds an instruction to the ROB if there is space
    public void add(Instruction instruction) {
        if(queue.isFull()) return;
        queue.enqueue(instruction);
    }

    /// Process instruction at the head of the ROB. True if instruction commited
    public boolean processHead() {
        if(queue.isEmpty()) return false;

        Optional<Instruction> item = queue.peek();

        if(item.isEmpty()) return false;

        Instruction instruction = item.get();

        // Only process the head if the instruction is ready
        if(!instruction.isReady()) return false;

        previous = instruction;

        handleInstruction(instruction);
        queue.dequeue();

        return true;
    }

    /// Add data (return true), or if not available, add source instruction for data (return false)
    public void initOperand(Operand operand) {
        // Register 0 is always equal to 0
        if(operand.register == 0) {
            operand.addData(0);
            return;
        }

        for (Iterator<Instruction> it = queue.reverseIterator(); it.hasNext(); ) {
            Instruction instruction = it.next();
            if(instruction instanceof RegisterWrite i && i.getDestination() == operand.register){
                if(i.hasResult()) operand.addData(i.getResult());
                else operand.addSource(i);

                return;
            }
        }

        // If no instruction with destination == operand.register, pull data from register
        operand.addData(registers.getRegister(operand.register));
    }

    /// Initialise the sources for a load instruction
    public void initLoad(LoadInstruction loadInstruction) {
        byte bytes = switch (loadInstruction) {
            case LHWInstruction _, LHWUInstruction _ -> 2;
            case LBInstruction _, LBUInstruction _ -> 1;
            default -> 4;
        };

        for (Iterator<Instruction> it = queue.reverseIterator(); it.hasNext(); ) {
            Instruction instruction = it.next();

            // If instruction is a load instruction in the correct range of addresses
            if(instruction instanceof SInstruction i &&
               i.getAddress() >= loadInstruction.getAddress() &&
               i.getAddress() < loadInstruction.getAddress() + bytes){
                loadInstruction.addSource(i);
            }
        }
    }

    /// Commit an instruction
    private void handleInstruction(Instruction instruction){
        switch (instruction){
            case Branch i -> {
                // If it has its result, and we shouldn't have branched, flush the pipeline
                if(branchUnit.branchMispredict(i)) flush();
            }

            case RegisterWrite i -> registers.setRegister(i.getDestination(), i.getResult());

            case MemoryWrite i -> memoryUnit.writeMemory(i);

            case Environment i -> halted = true;

            default -> {}
        }
    }

    /// Whether the ROB is full
    public boolean isFull(){
        return queue.isFull();
    }

    /// Whether the ROB is empty
    public boolean isEmpty(){
        return queue.isEmpty();
    }

    @Override
    public void flush(){
        // Don't actually have to remove data as resetting the pointers is enough
        queue.flush();
    }

    @Override
    public String toString() {
        return String.format("""
                    ROB:
                        Previous: %s
                        ------------------------------------
                        Instructions: %s
                """, previous, queue);
    }
}
