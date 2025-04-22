package org.example.processor.instructions;

import org.example.processor.commit.ROB;
import org.example.processor.data.Registers;

/// An operand for an instruction. Stores the required data, or the instruction to get it from
public class Operand {
    /// The register the data comes from
    public final byte register;

    /// The data from the associated register
    private int data = 0;

    /// Whether the data has been added for the operand
    private boolean hasData = false;

    /// The source of the data if it's not currently available
    private RegisterWrite source = null;

    /// Constructor for Operand
    public Operand(byte register) {
        this.register = register;
    }

    /// Add a source for the data
    public void addSource(RegisterWrite source) {
        if(source == null){
            throw new IllegalArgumentException("Source is null");
        }
        this.source = source;
    }

    /// Add data for the operand
    public void addData(int data) {
        // Only add data if data not already added
        if(hasData) return;

        this.data = data;
        this.hasData = true;
        this.source = null;
    }

    /// Get the data from source once it is ready
    public void getDataWhenAvailable() {
        if(hasData || !source.hasResult()) return;
//        System.out.println("Data now available from 0x" + Integer.toHexString(source.getPC()) + ": " + source.getResult());
        addData(source.getResult());
    }

    /// Whether the operand has the required data
    public boolean hasData() {
        return hasData;
    }

    /// Get the currently stored data for the operand
    public int getData() {
        return data;
    }

    @Override
    public String toString() {
        return String.format("Operand: %s(%s:%s)[%s]", register, hasData, getData(), source);
    }
}
