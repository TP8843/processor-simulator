package org.example.processor.executionUnits.builders;

import org.example.processor.buffers.Buffer;
import org.example.processor.executionUnits.Alu;
import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class AluBuilder {
    private final int quantity;

    private final List<Alu> aluList;

    public AluBuilder(Buffer<Instruction> input, int quantity) {
        this.quantity = quantity;
        this.aluList = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            this.aluList.add(new Alu(input));
        }
    }

    public int getQuantity() {
        return quantity;
    }

    /// Run execute cycle for all ALUs
    public void execute() {
        for (Alu alu : aluList) alu.execute();
    }
}
