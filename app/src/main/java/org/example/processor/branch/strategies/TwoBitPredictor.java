package org.example.processor.branch.strategies;

import org.example.processor.instructions.BInstructions.BInstruction;
import org.example.processor.instructions.Branch;

public class TwoBitPredictor implements BranchStrategy{
    /// The number of bits of the address to use
    private static final int BITS = 8;

    private static final int SIZE = 1 << BITS;

    /// Table of predicted values
    private final byte[] lookupTable = new byte[SIZE];

    @Override
    public boolean predict(BInstruction instruction) {
        int lookupAddress = instruction.getPC() & (SIZE - 1);
        int lookupValue = lookupTable[lookupAddress];

        System.out.printf("Current lookup value for %s: %s\n", lookupAddress, lookupValue);

        return lookupValue == 0b11 || lookupValue == 0b10;
    }

    @Override
    public void update(Branch instruction, boolean branched) {
        int lookupAddress = instruction.getPC() & (SIZE - 1);
        int lookupValue = lookupTable[lookupAddress];

        if(branched) {
            switch (lookupValue){
                case 0b00 -> lookupTable[lookupAddress] = 0b01;
                case 0b01, 0b10, 0b11 -> lookupTable[lookupAddress] = 0b11;
            }
        } else {
            switch (lookupValue){
                case 0b11 -> lookupTable[lookupAddress] = 0b10;
                case 0b10, 0b01, 0b00 -> lookupTable[lookupAddress] = 0b00;
            }
        }
    }
}
