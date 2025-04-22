package org.example;

import org.example.processor.*;
import org.example.processor.branch.BranchUnit;
import org.example.processor.buffers.*;
import org.example.processor.commit.MemoryWriteUnit;
import org.example.processor.commit.ROB;
import org.example.processor.data.Memory;
import org.example.processor.data.Registers;
import org.example.processor.executionUnits.*;
import org.example.processor.executionUnits.builders.*;
import org.example.processor.instructions.Environment;
import org.example.processor.instructions.IInstructions.EInstructions.EBreakInstruction;
import org.example.processor.instructions.IInstructions.EInstructions.ECallInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

import java.io.IOException;

public class Simulator {
    public final Config config;

    public final Memory memory = new Memory();
    public final Registers registers = new Registers();

    // Buffers
    public final Buffer<UndecodedInstruction> fetchDecodeBuffer = new MultiValueBuffer<>(16);
    public final Buffer<Instruction> decodeIssueBuffer = new MultiValueBuffer<>(16);
    public final BranchBuffer decodeBranchBuffer = new BranchBuffer();

    public final ReservationStation aluReservationStation = new ReservationStation(16);
    public final ReservationStation compareReservationStation = new ReservationStation(16);
    public final ReservationStation multiplyReservationStation = new ReservationStation(16);

    public final ManualReleaseReservationStation aguReservationStation = new ManualReleaseReservationStation(16);
    public final MemoryLoadBuffer aguLoadBuffer = new MemoryLoadBuffer(32);

    /// Initial buffers to flush for jumps and branches
    public final Flushable[] jumpBuffers = new Flushable[]{ fetchDecodeBuffer };

    /// Buffers to flush for a branch mispredict
    public final Flushable[] mispredictBuffers;

    public final AluBuilder alus;
    public final AguBuilder agus;
    public final LoadUnitBuilder loadUnits;
    public final CompareBuilder compareUnits;
    public final MultiplyBuilder multiplyUnits;

    public final MemoryWriteUnit memoryWriteUnit = new MemoryWriteUnit(memory);

    public final InstructionFetch instructionFetch = new InstructionFetch(memory, 0, fetchDecodeBuffer);
    public final BranchUnit branchUnit;

    public final EnvironmentHandler environmentHandler = new EnvironmentHandler(registers);
    public final ROB rob;
    public final Decode decode;
    public final IssueUnit issueUnit;
    
    /// Counts the number of instructions ran through the pipeline
    private int instructions = 0;


    public Simulator(Config config) {
        this.config = config;
        this.multiplyUnits = new MultiplyBuilder(multiplyReservationStation, config.multiply);

        this.mispredictBuffers = new Flushable[]{
                fetchDecodeBuffer,
                decodeBranchBuffer,
                decodeIssueBuffer,
                aluReservationStation,
                compareReservationStation,
                aguReservationStation,
                aguLoadBuffer,
                multiplyReservationStation,
                multiplyUnits
        };

        this.branchUnit = new BranchUnit(instructionFetch, decodeBranchBuffer, jumpBuffers, mispredictBuffers, fetchDecodeBuffer);
        this.rob = new ROB(branchUnit, memoryWriteUnit, registers, environmentHandler);
        this.decode = new Decode(fetchDecodeBuffer, decodeIssueBuffer, decodeBranchBuffer);
        this.issueUnit = new IssueUnit(
                decodeIssueBuffer,
                decodeBranchBuffer,
                aluReservationStation,
                compareReservationStation,
                aguReservationStation,
                multiplyReservationStation,
                rob);

        this.alus = new AluBuilder(aluReservationStation, config.alu);
        this.agus = new AguBuilder(aguReservationStation, aguLoadBuffer, rob, config.agu);
        this.compareUnits = new CompareBuilder(compareReservationStation, config.compare);
        this.loadUnits = new LoadUnitBuilder(memory, aguLoadBuffer, aguReservationStation, config.load);
    }
    
    public int getInstructions() {
        return instructions;
    }

    public void runCycle() {
        for (int i = 0; i < config.commitWidth; i++) {
            // Commit head of ROB
            if(rob.processHead()){
                instructions += 1;
            }
        }

        loadUnits.execute();

        aguLoadBuffer.addData();

        multiplyUnits.execute();
        compareUnits.execute();
        agus.execute();
        alus.execute();

        compareReservationStation.addData();
        aguReservationStation.addData();
        aluReservationStation.addData();
        multiplyReservationStation.addData();

        for (int i = 0; i < config.fetchDecodeWidth; i++) {
            // Branch and issue should happen in same cycle after decode (so in this order)
            // Stall fetching while jump / branch instruction is processing
            if(decode.decode()) fetchDecodeBuffer.stall();

            decodeBranchBuffer.addData();

            // Release fetching once address has been updated
            if(branchUnit.generateAddress()) {
                fetchDecodeBuffer.release();
            }

            issueUnit.issue();
        }

        for (int i = 0; i < config.fetchDecodeWidth; i++) {
            instructionFetch.process();
        }
    }

    static public Simulator createSimulator(String fileName, Config config) {
        Simulator simulator = new Simulator(config);
        simulator.memory.loadProgram(fileName, 0);

        return simulator;
    }
}
