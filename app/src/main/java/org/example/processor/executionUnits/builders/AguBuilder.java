package org.example.processor.executionUnits.builders;

import org.example.processor.buffers.Buffer;
import org.example.processor.commit.ROB;
import org.example.processor.executionUnits.Agu;
import org.example.processor.instructions.IInstructions.LoadInstructions.LoadInstruction;
import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.List;

public class AguBuilder {
    private final int quantity;

    private final List<Agu> aguList;

    public AguBuilder(Buffer<Instruction> input, Buffer<LoadInstruction> output, ROB rob, int quantity) {
        this.quantity = quantity;
        this.aguList = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            this.aguList.add(new Agu(input, output, rob));
        }
    }

    public int getQuantity() {
        return quantity;
    }

    /// Run execute cycle for all AGUs
    public void execute() {
        for (Agu agu : aguList) agu.execute();
    }
}
