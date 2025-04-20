package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.buffers.Flushable;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.RInstructions.multiply.*;

import java.util.Optional;

public class MultiplyUnit implements Flushable {
    /// Input to the multiply unit (normally a reservation station)
    public final Buffer<Instruction> input;

    /// The number of cycles until the operation is complete
    private int remainingCycles = 0;

    /// The instruction currently being processed
    private Instruction currentInstruction;

    /// The result to add to the instruction when the cycles run out
    private int currentResult;

    public MultiplyUnit(Buffer<Instruction> input) {
        this.input = input;
    }

    public void execute() {
        // Reduce count every clock cycle
        if(this.remainingCycles > 0){
            this.remainingCycles -= 1;
            return;
        }
        // Add result once processing is finished
        else if (this.currentInstruction instanceof MInstruction i) {
            i.addResult(this.currentResult);
            this.currentResult = 0;
            this.currentInstruction = null;
        }

        Optional<Instruction> value = input.pop();
        if(value.isEmpty()) return;

        Instruction instruction = value.get();

        switch (instruction) {
            case MULInstruction i -> {
                this.remainingCycles = 3;
                this.currentResult = (int)((long)i.rs1.getData() * (long)i.rs2.getData());
                this.currentInstruction = i;
            }
            case MULHighInstruction i -> {
                this.remainingCycles = 3;
                this.currentResult = (int)(((long)i.rs1.getData() * (long)i.rs2.getData()) >> 32);
                this.currentInstruction = i;
            }
            case MULHighUInstruction i -> {
                this.remainingCycles = 3;
                this.currentResult = (int)((Integer.toUnsignedLong(i.rs1.getData()) * Integer.toUnsignedLong(i.rs2.getData())) >> 32);
                this.currentInstruction = i;
            }
            case MULHIGHSUInstruction i -> {
                this.remainingCycles = 3;
                this.currentResult = (int)(((long) i.rs1.getData() * Integer.toUnsignedLong(i.rs2.getData())) >> 32);
                this.currentInstruction = i;
            }
            case DIVInstruction i -> {
                this.remainingCycles = 15;
                this.currentResult = i.rs1.getData() / i.rs2.getData();
                this.currentInstruction = i;
            }
            case DIVUInstruction i -> {
                this.remainingCycles = 15;
                this.currentResult = Integer.divideUnsigned(i.rs1.getData(), i.rs2.getData());
                this.currentInstruction = i;
            }
            case REMInstruction i -> {
                this.remainingCycles = 15;
                this.currentResult = i.rs1.getData() % i.rs2.getData();
                this.currentInstruction = i;
            }
            case REMUInstruction i -> {
                this.remainingCycles = 15;
                this.currentResult = Integer.remainderUnsigned(i.rs1.getData(), i.rs2.getData());
                this.currentInstruction = i;
            }
            default -> {}
        }
    }

    @Override
    public void flush() {
        this.remainingCycles = 0;
        this.currentInstruction = null;
        this.currentResult = 0;
    }
}
