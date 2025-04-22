package org.example.processor.executionUnits.builders;

import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.ManualReleaseReservationStation;
import org.example.processor.data.Memory;
import org.example.processor.executionUnits.Alu;
import org.example.processor.executionUnits.MemoryLoadUnit;
import org.example.processor.instructions.IInstructions.LoadInstructions.LoadInstruction;
import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class LoadUnitBuilder {
    private final int quantity;

    private final List<MemoryLoadUnit> mluList;

    public LoadUnitBuilder(Memory memory, Buffer<LoadInstruction> input, ManualReleaseReservationStation reservationStation, int quantity) {
        this.quantity = quantity;
        this.mluList = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            this.mluList.add(new MemoryLoadUnit(memory, input, reservationStation));
        }
    }

    public int getQuantity() {
        return quantity;
    }

    /// Run execute cycle for all ALUs
    public void execute() {
        for (MemoryLoadUnit mlu : mluList) mlu.execute();
    }
}
