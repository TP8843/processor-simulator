package org.example.processor.executionUnits.builders;

import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.Flushable;
import org.example.processor.executionUnits.MultiplyUnit;
import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class MultiplyBuilder implements Flushable {
    private final int quantity;

    private final List<MultiplyUnit> multiplyList;

    public MultiplyBuilder(Buffer<Instruction> input, int quantity) {
        this.quantity = quantity;
        this.multiplyList = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            this.multiplyList.add(new MultiplyUnit(input));
        }
    }

    public int getQuantity() {
        return quantity;
    }

    /// Run execute cycle for all ALUs
    public void execute() {
        for (MultiplyUnit multiplyUnit : multiplyList) multiplyUnit.execute();
    }

    @Override
    public void flush() {
        for (MultiplyUnit multiplyUnit : multiplyList) multiplyUnit.flush();
    }
}
