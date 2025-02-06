package org.example;

import org.example.processor.instructions.JInstruction;

public class Main {
    public static void main(String[] args) {
        int instruction = 0x03c000ef;
//        
//        System.out.println(String.format("Loaded instruction %32s",
//                String.format("%32s", Integer.toBinaryString(instruction)).replace(' ', '0')));
//        
//        System.out.println(Integer.toBinaryString(JInstruction.decodeImmediate(instruction)));
        
        if (args.length == 0) {
            System.out.println("Must include a file path for an assembly file");
            return;
        }

        Simulator simulator = Simulator.createSimulator(args[0]);
        simulator.runSimulator();
    }
}