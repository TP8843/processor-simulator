package org.example;

import org.example.processor.Decode;
import org.example.processor.Registers;
import org.example.processor.instructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstruction;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must include a file path for an assembly file");
            return;
        }

        Simulator simulator = Simulator.createSimulator(args[0]);
        Debugger debugger = new Debugger(simulator);
        debugger.run();
    }
}