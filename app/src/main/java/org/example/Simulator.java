package org.example;

import org.example.processor.*;
import org.example.processor.buffers.*;
import org.example.processor.executionUnits.Alu;
import org.example.processor.executionUnits.CompareUnit;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

import java.util.ArrayList;
import java.util.List;

public class Simulator {
    public final Memory memory = new Memory();
    public final Registers registers = new Registers();

    // Buffers
    public final Buffer<UndecodedInstruction> fetchDecodeBuffer = new SingleValueBuffer<>();
    public final Buffer<Instruction> decodeIssueBuffer = new SingleValueBuffer<>();
    public final DataBlockingBuffer decodeBranchBuffer = new DataBlockingBuffer();
    public final ReservationStation aluReservationStation = new ReservationStation(registers);
    public final ReservationStation compareReservationStation = new ReservationStation(registers);
    public final Buffer<Instruction> compareBranchBuffer = new SingleValueBuffer<>();
    public final Buffer<Instruction> aluMemoryBuffer = new SingleValueBuffer<>();
    public final Buffer<Instruction> memoryWriteBackBuffer = new SingleValueBuffer<>();

    // Buffers to flush for jumps and branches
    public final Flushable[] jumpBuffers = new Flushable[]{ fetchDecodeBuffer };
    public final Flushable[] branchBuffers = new Flushable[]{ fetchDecodeBuffer, aluReservationStation, compareReservationStation, aluMemoryBuffer };

    public final InstructionFetch instructionFetch = new InstructionFetch(memory, 8, fetchDecodeBuffer);
    public final Decode decode = new Decode(registers, fetchDecodeBuffer, decodeIssueBuffer, decodeBranchBuffer);
    public final IssueUnit issueUnit = new IssueUnit(registers, decodeIssueBuffer, aluReservationStation, compareReservationStation);
    public final Alu alu = new Alu(aluReservationStation, aluMemoryBuffer);
    public final CompareUnit compareUnit = new CompareUnit(compareReservationStation, compareBranchBuffer);
    public final BranchUnit branchUnit = new BranchUnit(instructionFetch, decodeBranchBuffer, jumpBuffers, branchBuffers);
    public final MemoryAccessUnit memoryAccessUnit = new MemoryAccessUnit(memory, aluMemoryBuffer, memoryWriteBackBuffer);
    public final WriteBackUnit writeBackUnit = new WriteBackUnit(registers, memoryWriteBackBuffer);
    
    /// Counts the number of instructions ran through the pipeline
    private int instructions = 0;
    
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
        
        alu.execute();
        aluReservationStation.addData();
        
        compareUnit.execute();
        compareReservationStation.addData();
        
        issueUnit.issue();

        if(decode.decode()){
            System.out.println("Stalling fetch decode buffer");
            fetchDecodeBuffer.stall();
        }

        decodeBranchBuffer.addData(registers);

        if(branchUnit.updatePC() || branchUnit.generateAddress()){
            System.out.println("Releasing fetch decode buffer");
            fetchDecodeBuffer.release();
            fetchDecodeBuffer.flush();
        }
        
        instructionFetch.process();
    }

    static public Simulator createSimulator(String fileName) {
        Simulator simulator = new Simulator();
        simulator.memory.loadProgram(fileName, 8);

        return simulator;
    }
}
