package org.example;

import org.example.processor.*;
import org.example.processor.branch.BranchUnit;
import org.example.processor.buffers.*;
import org.example.processor.commit.MemoryWriteUnit;
import org.example.processor.commit.ROB;
import org.example.processor.data.Memory;
import org.example.processor.data.Registers;
import org.example.processor.executionUnits.builders.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

public class Simulator {
    public final Config config;

    public final Memory memory = new Memory();
    public final Registers registers = new Registers();

    // Buffers
    public final Buffer<UndecodedInstruction> fetchDecodeBuffer;
    public final Buffer<Instruction> decodeIssueBuffer;
    public final BranchBuffer issueBranchBuffer;

    public final ReservationStation aluReservationStation;
    public final ReservationStation compareReservationStation;
    public final ReservationStation multiplyReservationStation;

    public final ManualReleaseReservationStation aguReservationStation;
    public final MemoryLoadBuffer aguLoadBuffer;

    /// Initial buffers to flush for jumps and branches
    public final Flushable[] jumpBuffers;

    /// Buffers to flush for a branch mispredict
    public final Flushable[] mispredictBuffers;

    public final AluBuilder alus;
    public final AguBuilder agus;
    public final LoadUnitBuilder loadUnits;
    public final CompareBuilder compareUnits;
    public final MultiplyBuilder multiplyUnits;

    public final MemoryWriteUnit memoryWriteUnit = new MemoryWriteUnit(memory);

    public final InstructionFetch instructionFetch;
    public final BranchUnit branchUnit;

    public final EnvironmentHandler environmentHandler = new EnvironmentHandler(registers);
    public final ROB rob;
    public final Decode decode;
    public final IssueUnit issueUnit;
    
    /// Counts the number of instructions ran through the pipeline
    private int instructions = 0;


    public Simulator(Config config) {
        this.config = config;

        this.fetchDecodeBuffer = new MultiValueBuffer<>(config.fetchDecodeBuffer);
        this.decodeIssueBuffer = new MultiValueBuffer<>(config.decodeIssueBuffer);
        this.issueBranchBuffer = new BranchBuffer();

        this.aluReservationStation = new ReservationStation(config.aluRs);
        this.compareReservationStation = new ReservationStation(config.compareRs);
        this.multiplyReservationStation = new ReservationStation(config.multiplyRs);

        this.aguReservationStation = new ManualReleaseReservationStation(config.aguRs);
        this.aguLoadBuffer = new MemoryLoadBuffer(config.aguLoadBuffer);

        this.instructionFetch = new InstructionFetch(memory, 0, fetchDecodeBuffer);

        this.jumpBuffers = new Flushable[]{ fetchDecodeBuffer };

        this.multiplyUnits = new MultiplyBuilder(multiplyReservationStation, config.multiply);

        this.mispredictBuffers = new Flushable[]{
                fetchDecodeBuffer,
                issueBranchBuffer,
                decodeIssueBuffer,
                aluReservationStation,
                compareReservationStation,
                aguReservationStation,
                aguLoadBuffer,
                multiplyReservationStation,
                multiplyUnits
        };

        this.branchUnit = new BranchUnit(instructionFetch, issueBranchBuffer, jumpBuffers, mispredictBuffers, fetchDecodeBuffer);
        this.rob = new ROB(branchUnit, memoryWriteUnit, registers, environmentHandler);
        this.decode = new Decode(fetchDecodeBuffer, decodeIssueBuffer, issueBranchBuffer);
        this.issueUnit = new IssueUnit(
                decodeIssueBuffer,
                issueBranchBuffer,
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

            issueBranchBuffer.addData();

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
