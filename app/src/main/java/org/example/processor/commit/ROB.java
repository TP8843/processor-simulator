package org.example.processor.commit;

import org.example.EnvironmentHandler;
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

    /// Handles environment calls
    private final EnvironmentHandler environmentHandler;

    public ROB(BranchUnit branchUnit, MemoryWriteUnit memoryUnit, Registers registers, EnvironmentHandler environmentHandler) {
        this(branchUnit, memoryUnit, registers, environmentHandler, 128);
    }

    public ROB(BranchUnit branchUnit, MemoryWriteUnit memoryUnit, Registers registers, EnvironmentHandler environmentHandler, int size) {
        this.branchUnit = branchUnit;
        this.memoryUnit = memoryUnit;
        this.registers = registers;
        this.environmentHandler = environmentHandler;
        this.size = size;
        this.queue = new CircularQueue<>(size);
    }

    /// Gets instruction just commited by ROB
    public Instruction getPrevious() {
        return previous;
    }

    /// Adds an instruction to the ROB if there is space
    public void add(Instruction instruction) {
        if(queue.isFull()) throw new IllegalStateException("Queue is full");

        instruction.initOperands(this);
        queue.enqueue(instruction);

//        System.out.println("Added " + Integer.toHexString(instruction.getPC()) + " to ROB");

//        if(instruction.getPC() == 0x140){
//            System.out.println("Adding sus: " + instruction);
//        }
    }

    /// Process instruction at the head of the ROB. True if instruction commited
    public boolean processHead() {
        if(queue.isEmpty()) return false;

        Optional<Instruction> item = queue.peek();

        if(item.isEmpty()) return false;

        Instruction instruction = item.get();

        // Only process the head if the instruction is ready
        if(!instruction.isReady()) return false;

        queue.dequeue();
        previous = instruction;

        handleInstruction(instruction);

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
//            System.out.printf("Checking instruction 0x%x for matching destination\n", instruction.getPC());
            if(instruction instanceof RegisterWrite i && i.getDestination() == operand.register){
//                System.out.printf("Found instruction 0x%x with matching destination. Adding as source and returning\n", i.getPC());
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
//        System.out.println("Initializing load 0x" + Integer.toHexString(loadInstruction.getPC()));

        for(Iterator<Instruction> it = queue.reverseIterator(); it.hasNext(); ) {
            Instruction instruction = it.next();

            if(instruction instanceof SInstruction s){
                loadInstruction.addInitialSource(s);
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

            case RegisterWrite i -> {
//                if(i instanceof AddIInstruction add){
//                    System.out.printf("Commiting add at 0x%s: %s + %s = %s\n", Integer.toHexString(add.getPC()), add.rs1.getData(), add.imm, add.getResult());
//                }

                registers.setRegister(i.getDestination(), i.getResult());
            }

            case MemoryWrite i -> memoryUnit.writeMemory(i);

            case Environment i -> environmentHandler.processEnvironment(i);

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
