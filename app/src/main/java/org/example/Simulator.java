package org.example;

import org.example.processor.*;
import org.example.processor.buffers.*;
import org.example.processor.commit.MemoryWriteUnit;
import org.example.processor.commit.ROB;
import org.example.processor.data.Memory;
import org.example.processor.data.Registers;
import org.example.processor.executionUnits.*;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.UndecodedInstruction;

public class Simulator {
    public final Memory memory = new Memory();
    public final Registers registers = new Registers();

    // Buffers
    public final Buffer<UndecodedInstruction> fetchDecodeBuffer = new MultiValueBuffer<>(16);
    public final Buffer<Instruction> decodeIssueBuffer = new MultiValueBuffer<>(16);
    public final DataBlockingBuffer decodeBranchBuffer = new DataBlockingBuffer();

    public final ReservationStation aluReservationStation = new ReservationStation(16);
    public final ReservationStation compareReservationStation = new ReservationStation(16);
    public final ReservationStation multiplyReservationStation = new ReservationStation(16);

    public final ReservationStation aguReservationStation = new ReservationStation(16);
    public final ReservationStation aguLoadBuffer = new ReservationStation(16);

    /// Initial buffers to flush for jumps and branches
    public final Flushable[] jumpBuffers = new Flushable[]{ fetchDecodeBuffer };

    /// Buffers to flush for a branch mispredict
    public final Flushable[] mispredictBuffers = new Flushable[]{
            fetchDecodeBuffer,
            decodeBranchBuffer,
            decodeIssueBuffer,
            aluReservationStation,
            compareReservationStation,
            aguReservationStation,
            multiplyReservationStation
    };

    public final Alu alu = new Alu(aluReservationStation);
    public final CompareUnit compareUnit = new CompareUnit(compareReservationStation);
    public final MultiplyUnit multiplyUnit = new MultiplyUnit(multiplyReservationStation);

    public final MemoryWriteUnit memoryWriteUnit = new MemoryWriteUnit(memory);

    public final InstructionFetch instructionFetch = new InstructionFetch(memory, 8, fetchDecodeBuffer);
    public final BranchUnit branchUnit = new BranchUnit(instructionFetch, decodeBranchBuffer, jumpBuffers, mispredictBuffers, fetchDecodeBuffer);

    public final ROB rob = new ROB(branchUnit, memoryWriteUnit, registers);
    public final Decode decode = new Decode(registers, rob, fetchDecodeBuffer, decodeIssueBuffer, decodeBranchBuffer);
    public final IssueUnit issueUnit = new IssueUnit(
            decodeIssueBuffer,
            aluReservationStation,
            compareReservationStation,
            aguReservationStation,
            multiplyReservationStation,
            rob);

    public final Agu agu = new Agu(aguReservationStation, aguLoadBuffer, rob);
    public final MemoryLoadUnit memoryLoadUnit = new MemoryLoadUnit(memory, aguLoadBuffer);
    
    /// Counts the number of instructions ran through the pipeline
    private int instructions = 0;
    
    public int getInstructions() {
        return instructions;
    }

    public void runCycle() {
        for (int i = 0; i < 4; i++) {
            // Commit head of ROB
            if(rob.processHead()){
                instructions += 1;
                System.out.println("Commited instruction 0x" + Integer.toHexString(rob.getPrevious().getPC()));
            }
        }

        compareUnit.execute();

        memoryLoadUnit.execute();
        aguLoadBuffer.addData();
        agu.execute();

        multiplyUnit.execute();

        for (int i = 0; i < 4; i++) {
            alu.execute();
        }

        compareReservationStation.addData();
        aguReservationStation.addData();
        aluReservationStation.addData();
        multiplyReservationStation.addData();

        for (int i = 0; i < 4; i++) {
            // Branch and issue should happen in same cycle after decode (so in this order)
            // Stall fetching while jump / branch instruction is processing
            if(decode.decode()){
                fetchDecodeBuffer.stall();
                System.out.println("Yaas, it's stallin' time: " + decodeBranchBuffer.peek().get());
            }

            decodeBranchBuffer.addData();

            // Release fetching once address has been updated
            if(branchUnit.generateAddress()){
                fetchDecodeBuffer.release();
                System.out.println("OMG, releasing the stall fr");
            }

            issueUnit.issue();
        }

        for (int i = 0; i < 4; i++) {
            instructionFetch.process();
        }
    }

    static public Simulator createSimulator(String fileName) {
        Simulator simulator = new Simulator();
        simulator.memory.loadProgram(fileName, 8);

        return simulator;
    }
}
