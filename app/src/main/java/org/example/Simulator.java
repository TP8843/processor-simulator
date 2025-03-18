package org.example;

import org.example.processor.*;
import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.Flushable;
import org.example.processor.buffers.ReservationStation;
import org.example.processor.buffers.SingleValueBuffer;
import org.example.processor.executionUnits.Alu;
import org.example.processor.executionUnits.CompareUnit;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
    public final Memory memory;
    public final Registers registers;

    public final Buffer<UndecodedInstruction> fetchDecodeBuffer;
    public final Buffer<Instruction> decodeIssueBuffer;
    public final ReservationStation aluReservationStation;
    public final ReservationStation compareReservationStation;
    public final Buffer<Instruction> compareBranchBuffer;
    public final Buffer<Instruction> aluBranchBuffer;
    public final Buffer<Instruction> aluMemoryBuffer;
    public final Buffer<Instruction> memoryWriteBackBuffer;
    
    public final InstructionFetch instructionFetch;
    public final Decode decode;
    public final IssueUnit issueUnit;
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
                     Buffer<Instruction> decodeIssueBuffer,
                     IssueUnit issueUnit,
                     ReservationStation aluReservationStation,
                     ReservationStation compareReservationStation,
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
        this.issueUnit = issueUnit;
        this.decodeIssueBuffer = decodeIssueBuffer;
        this.aluReservationStation = aluReservationStation;
        this.compareReservationStation = compareReservationStation;
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
        aluReservationStation.addData();
        
        compareUnit.execute();
        compareReservationStation.addData();
        
        issueUnit.issue();

        branchUnit.updatePC();
        branchUnit.generateAddress();

        decode.decode();

        // Stop fetching of instructions until branching instruction finishes
        if(compareReservationStation.hasValue() && compareReservationStation.peek().get().canBranch()) {
            fetchDecodeBuffer.stall();
        }
        
        instructionFetch.process();
    }

    static public Simulator createSimulator(String fileName) {
        Memory memory = new Memory();
        Registers registers = new Registers();
        
        Buffer<UndecodedInstruction> fetchDecodeBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> decodeIssueBuffer = new SingleValueBuffer<>();
        ReservationStation aluReservationStation = new ReservationStation(registers);
        ReservationStation compareReservationStation = new ReservationStation(registers);
        Buffer<Instruction> compareBranchBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> decodeBranchBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> aluMemoryBuffer = new SingleValueBuffer<>();
        Buffer<Instruction> memoryWriteBackBuffer = new SingleValueBuffer<>();
        
        InstructionFetch instructionFetch = new InstructionFetch(memory, 8, fetchDecodeBuffer);
        Decode decode = new Decode(registers, fetchDecodeBuffer, decodeIssueBuffer, decodeBranchBuffer);
        IssueUnit issueUnit = new IssueUnit(registers, decodeIssueBuffer, aluReservationStation, compareReservationStation);
        Alu alu = new Alu(aluReservationStation, aluMemoryBuffer);
        CompareUnit compareUnit = new CompareUnit(compareReservationStation, compareBranchBuffer);

        List<Flushable> jumpBuffers = new ArrayList<>();
        jumpBuffers.add(fetchDecodeBuffer);
        
        List<Flushable> branchBuffers = new ArrayList<>();
        branchBuffers.add(fetchDecodeBuffer);
        branchBuffers.add(aluReservationStation);
        branchBuffers.add(compareReservationStation);
        
        BranchUnit branchUnit = new BranchUnit(
                instructionFetch, 
                compareBranchBuffer, 
                decodeBranchBuffer,
                jumpBuffers,
                branchBuffers);
        
        MemoryAccessUnit memoryAccessUnit = new MemoryAccessUnit(memory, aluMemoryBuffer, memoryWriteBackBuffer);
        WriteBackUnit writeBackUnit = new WriteBackUnit(registers, memoryWriteBackBuffer);

        memory.loadProgram(fileName, 8);

        return new Simulator(
                memory,
                registers,
                instructionFetch,
                fetchDecodeBuffer,
                decode,
                decodeIssueBuffer,
                issueUnit,
                aluReservationStation,
                compareReservationStation,
                alu,
                compareUnit,
                compareBranchBuffer,
                decodeBranchBuffer,
                branchUnit,
                aluMemoryBuffer,
                memoryAccessUnit,
                memoryWriteBackBuffer,
                writeBackUnit
        );
    }
}
