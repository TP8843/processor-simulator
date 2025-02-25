package org.example;

import org.example.processor.*;
import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.DataBlockingBuffer;
import org.example.processor.buffers.SingleValueBuffer;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

public class Simulator {
    public final Memory memory;
    public final Registers registers;

    public final Buffer<UndecodedInstruction> fetchDecodeBuffer;
    public final DataBlockingBuffer decodeAluBuffer;
    public final DataBlockingBuffer decodeCompareBuffer;
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
    
    /// Whether branch store currently occurring
    private boolean branchStall = false;
    
    /// Counts the number of instructions ran through the pipeline
    private int instructions = 0;

    public Simulator(Memory memory,
                     Registers registers,
                     InstructionFetch instructionFetch,
                     Buffer<UndecodedInstruction> fetchDecodeBuffer,
                     Decode decode,
                     DataBlockingBuffer decodeAluBuffer,
                     Alu alu,
                     DataBlockingBuffer decodeCompareBuffer,
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
        this.decodeAluBuffer = decodeAluBuffer;
        this.alu = alu;
        this.decodeCompareBuffer = decodeCompareBuffer;
        this.compareUnit = compareUnit;
        this.compareBranchBuffer = compareBranchBuffer;
        this.aluBranchBuffer = aluBranchBuffer;
        this.branchUnit = branchUnit;
        this.aluMemoryBuffer = aluMemoryBuffer;
        this.memoryAccessUnit = memoryAccessUnit;
        this.memoryWriteBackBuffer = memoryWriteBackBuffer;
        this.writeBackUnit = writeBackUnit;
    }
    
    public boolean getBranchStall() {
        return branchStall;
    }
    
    public int getInstructions() {
        return instructions;
    }

    public void runCycle() {
        if (writeBackUnit.writeBack())
            instructions += 1;

        memoryAccessUnit.process();
        writeBackUnit.input = memoryAccessUnit.output;
        
        // Once branch unit has updated the PC, instruction fetch can fetch again :D
        if(branchUnit.updatePC()) {
            fetchDecodeBuffer.flush();
            fetchDecodeBuffer.release();
        }

        alu.execute();
        compareUnit.execute();

        decode.decode();

        // Stop fetching of instructions until branching instruction finishes
        if(decodeAluBuffer.hasValue() && decodeAluBuffer.peek().get().canBranch()) {
            fetchDecodeBuffer.stall();
        }
        
        instructionFetch.process();
    }

    static public Simulator createSimulator(String fileName) {
        Buffer<UndecodedInstruction> fetchDecodeBuffer = new SingleValueBuffer<>();
        DataBlockingBuffer decodeAluBuffer = new DataBlockingBuffer();
        DataBlockingBuffer decodeCompareBuffer = new DataBlockingBuffer();
        Buffer<Instruction> compareBranchBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> aluBranchBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> aluMemoryBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> memoryWriteBackBuffer = new SingleValueBuffer<>();
        
        Memory memory = new Memory();
        Registers registers = new Registers();
        InstructionFetch instructionFetch = new InstructionFetch(memory, 8, fetchDecodeBuffer);
        Decode decode = new Decode(registers, fetchDecodeBuffer, decodeAluBuffer, decodeCompareBuffer);
        Alu alu = new Alu(decodeAluBuffer, aluMemoryBuffer, aluBranchBuffer);
        CompareUnit compareUnit = new CompareUnit(decodeCompareBuffer, compareBranchBuffer);
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
                decodeAluBuffer,
                alu,
                decodeCompareBuffer,
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
