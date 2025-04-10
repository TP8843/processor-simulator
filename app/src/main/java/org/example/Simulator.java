package org.example;

import org.example.processor.*;
import org.example.processor.buffers.*;
import org.example.processor.commit.MemoryWriteUnit;
import org.example.processor.commit.ROB;
import org.example.processor.data.Memory;
import org.example.processor.data.Registers;
import org.example.processor.executionUnits.Agu;
import org.example.processor.executionUnits.Alu;
import org.example.processor.executionUnits.CompareUnit;
import org.example.processor.executionUnits.MemoryLoadUnit;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

public class Simulator {
    public final Memory memory = new Memory();
    public final Registers registers = new Registers();

    // Buffers
    public final Buffer<UndecodedInstruction> fetchDecodeBuffer = new MultiValueBuffer<>(4);
    public final Buffer<Instruction> decodeIssueBuffer = new SingleValueBuffer<>();
    public final DataBlockingBuffer decodeBranchBuffer = new DataBlockingBuffer();

    public final ReservationStation aluReservationStation = new ReservationStation();
    public final ReservationStation compareReservationStation = new ReservationStation();

    public final ReservationStation aguReservationStation = new ReservationStation();
    public final Buffer<Instruction> aguLoadBuffer = new MultiValueBuffer<>(4);

    /// Initial buffers to flush for jumps and branches
    public final Flushable[] jumpBuffers = new Flushable[]{ fetchDecodeBuffer };

    /// Buffers to flush for a branch mispredict
    public final Flushable[] mispredictBuffers = new Flushable[]{
            fetchDecodeBuffer,
            decodeBranchBuffer,
            decodeIssueBuffer,
            aluReservationStation,
            compareReservationStation,
            aguReservationStation
    };

    public final Agu agu = new Agu(aguReservationStation, aguLoadBuffer);
    public final MemoryLoadUnit memoryLoadUnit = new MemoryLoadUnit(memory, aguLoadBuffer);

    public final Alu alu = new Alu(aluReservationStation);
    public final CompareUnit compareUnit = new CompareUnit(compareReservationStation);

    public final MemoryWriteUnit memoryWriteUnit = new MemoryWriteUnit(memory);

    public final InstructionFetch instructionFetch = new InstructionFetch(memory, 8, fetchDecodeBuffer);
    public final BranchUnit branchUnit = new BranchUnit(instructionFetch, decodeBranchBuffer, jumpBuffers, mispredictBuffers);

    public final ROB rob = new ROB(branchUnit, memoryWriteUnit, registers);
    public final Decode decode = new Decode(registers, rob, fetchDecodeBuffer, decodeIssueBuffer, decodeBranchBuffer);
    public final IssueUnit issueUnit = new IssueUnit(registers, decodeIssueBuffer, aluReservationStation, compareReservationStation, aguReservationStation, rob);
    
    /// Counts the number of instructions ran through the pipeline
    private int instructions = 0;
    
    public int getInstructions() {
        return instructions;
    }

    public void runCycle() {
        // Commit head of ROB
        if(rob.processHead()){
            instructions += 1;
            System.out.println("Commited instruction 0x" + Integer.toHexString(rob.getPrevious().getPC()));
        }

        compareUnit.execute();

        memoryLoadUnit.execute();
        agu.execute();
        
        alu.execute();

        compareReservationStation.addData();
        aguReservationStation.addData();
        aluReservationStation.addData();

        // Branch and issue should happen in same cycle after decode (so in this order)

        // Stall fetching while jump / branch instruction is processing
        if(decode.decode())
            fetchDecodeBuffer.stall();

        decodeBranchBuffer.addData();

        // Release fetching once address has been updated
        if(branchUnit.generateAddress())
            fetchDecodeBuffer.release();

        issueUnit.issue();
        
        instructionFetch.process();
    }

    static public Simulator createSimulator(String fileName) {
        Simulator simulator = new Simulator();
        simulator.memory.loadProgram(fileName, 8);

        return simulator;
    }
}
