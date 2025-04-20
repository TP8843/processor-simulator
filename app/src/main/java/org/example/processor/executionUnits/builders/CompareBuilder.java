package org.example.processor.executionUnits.builders;

import org.example.processor.buffers.Buffer;
import org.example.processor.executionUnits.CompareUnit;
import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class CompareBuilder {
    private final int quantity;

    private final List<CompareUnit> compareList;

    public CompareBuilder(Buffer<Instruction> input, int quantity) {
        this.quantity = quantity;
        this.compareList = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            this.compareList.add(new CompareUnit(input));
        }
    }

    public int getQuantity() {
        return quantity;
    }

    /// Run execute cycle for all ALUs
    public void execute() {
        for (CompareUnit compare : compareList) compare.execute();
    }
}
