package org.example.processor.executionUnits;

import org.example.processor.buffers.ManualReleaseReservationStation;
import org.example.processor.data.Memory;
import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.Instruction;

import java.util.Optional;

public class MemoryLoadUnit {
    /// Memory to load data from
    private final Memory memory;

    /// Reservation station to release when instruction finishes processing
    private final ManualReleaseReservationStation reservationStation;

    /// Input buffer for instructions (generally will come from AGU)
    private final Buffer<LoadInstruction> input;

    public MemoryLoadUnit(Memory memory, Buffer<LoadInstruction> input, ManualReleaseReservationStation reservationStation) {
        this.memory = memory;
        this.input = input;
        this.reservationStation = reservationStation;
    }

    public void execute(){
        Optional<LoadInstruction> value = input.pop();
        if(value.isEmpty()) return;
        LoadInstruction instruction = value.get();

        reservationStation.release();

        int memoryMask = instruction.getMemoryMask();

        if(memoryMask == 0xffffffff) {
            instruction.addResult(instruction.getResult());
            return;
        }

        int memoryLoad = switch (instruction) {
            case LBInstruction _ -> memory.getByte(instruction.getAddress(), false);
            case LBUInstruction _ -> memory.getByte(instruction.getAddress(), true);
            case LHWInstruction _ -> memory.getHalfWord(instruction.getAddress(), false);
            case LHWUInstruction _ -> memory.getHalfWord(instruction.getAddress(), true);
            case LWInstruction _ -> memory.getWord(instruction.getAddress());

            default -> 0;
        };

        instruction.addResult(instruction.getResult() | memoryLoad & ~memoryMask);
    }
}
