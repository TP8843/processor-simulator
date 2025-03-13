package org.example;

import org.example.processor.*;
import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.ReservationStation;
import org.example.processor.buffers.SingleValueBuffer;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

public class Simulator {
    public final Memory memory;
    public final Registers registers;

    public final Buffer<UndecodedInstruction> fetchDecodeBuffer;
    public final ReservationStation decodeBuffer;
    public final Buffer<Instruction> compareBranchBuffer;
    public final Buffer<Instruction> aluBranchBuffer;
    public final Buffer<Instruction> aluMemoryBuffer;
    public final Buffer<Instruction> memoryWriteBackBuffer;
    
    public final InstructionFetch instructionFetch;
    public final Decode decode;
    public final Alu alu;
    public final CompareUnit compareUnit;
    public final BranchUnit branchUnit;
    public final MemoryAccessUnit memoryAccessUnit;
    public final WriteBackUnit writeBackUnit;
    
    /// Counts the number of instructions ran through the pipeline
    private int instructions = 0;

    public Simulator(Memory memory,
                     Registers registers,
                     InstructionFetch instructionFetch,
                     Buffer<UndecodedInstruction> fetchDecodeBuffer,
                     Decode decode,
                     ReservationStation decodeBuffer,
                     Alu alu,
                     CompareUnit compareUnit,
                     Buffer<Instruction> compareBranchBuffer,
                     Buffer<Instruction> aluBranchBuffer,
                     BranchUnit branchUnit,
                     Buffer<Instruction> aluMemoryBuffer,
                     MemoryAccessUnit memoryAccessUnit,
                     Buffer<Instruction> memoryWriteBackBuffer,
                     WriteBackUnit writeBackUnit) {
        this.memory = memory;
        this.registers = registers;
        this.instructionFetch = instructionFetch;
        this.fetchDecodeBuffer = fetchDecodeBuffer;
        this.decode = decode;
        this.decodeBuffer = decodeBuffer;
        this.alu = alu;
        this.compareUnit = compareUnit;
        this.compareBranchBuffer = compareBranchBuffer;
        this.aluBranchBuffer = aluBranchBuffer;
        this.branchUnit = branchUnit;
        this.aluMemoryBuffer = aluMemoryBuffer;
        this.memoryAccessUnit = memoryAccessUnit;
        this.memoryWriteBackBuffer = memoryWriteBackBuffer;
        this.writeBackUnit = writeBackUnit;
    }
    
    public int getInstructions() {
        return instructions;
    }

    public void runCycle() {
        if (writeBackUnit.writeBack()){
            instructions += 1;
            System.out.println("Finished processing instruction 0x" + 
                    Integer.toHexString(writeBackUnit.previous.getPC()));
        }

        memoryAccessUnit.process();
        writeBackUnit.input = memoryAccessUnit.output;
        
        // Once branch unit has updated the PC, instruction fetch can fetch again :D
        if(aluBranchBuffer.hasValue() && aluBranchBuffer.peek().get().canBranch()) {
            fetchDecodeBuffer.release();
        }

        alu.execute();
        compareUnit.execute();
        
        // Add data to current instruction in the buffer, and reserve the destination in the ALU buffer
        // TODO: Reserve destination when ready in buffer
        decodeBuffer.addData();

        branchUnit.generateAddress();

        decode.decode();

        // Stop fetching of instructions until branching instruction finishes
        if(decodeBuffer.hasValue() && decodeBuffer.peek().get().canBranch()) {
            fetchDecodeBuffer.stall();
        }
        
        instructionFetch.process();
    }

    static public Simulator createSimulator(String fileName) {
        Memory memory = new Memory();
        Registers registers = new Registers();
        
        Buffer<UndecodedInstruction> fetchDecodeBuffer = new SingleValueBuffer<>();
        ReservationStation decodeBuffer = new ReservationStation(registers);
        Buffer<Instruction> compareBranchBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> aluBranchBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> aluMemoryBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> memoryWriteBackBuffer = new SingleValueBuffer<>();
        
        InstructionFetch instructionFetch = new InstructionFetch(memory, 8, fetchDecodeBuffer);
        Decode decode = new Decode(registers, fetchDecodeBuffer, decodeBuffer);
        Alu alu = new Alu(decodeBuffer, aluMemoryBuffer, aluBranchBuffer);
        CompareUnit compareUnit = new CompareUnit(decodeBuffer, compareBranchBuffer);
        BranchUnit branchUnit = new BranchUnit(instructionFetch, compareBranchBuffer, aluBranchBuffer);
        MemoryAccessUnit memoryAccessUnit = new MemoryAccessUnit(memory, aluMemoryBuffer, memoryWriteBackBuffer);
        WriteBackUnit writeBackUnit = new WriteBackUnit(registers, memoryWriteBackBuffer);

        memory.loadProgram(fileName, 8);

        return new Simulator(
                memory,
                registers,
                instructionFetch,
                fetchDecodeBuffer,
                decode,
                decodeBuffer,
                alu,
                compareUnit,
                compareBranchBuffer,
                aluBranchBuffer,
                branchUnit,
                aluMemoryBuffer,
                memoryAccessUnit,
                memoryWriteBackBuffer,
                writeBackUnit
        );
    }
}
