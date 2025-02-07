package org.example;

import org.example.processor.Decode;
import org.example.processor.Registers;
import org.example.processor.instructions.IInstruction;
import org.example.processor.instructions.Instruction;
import org.example.processor.instructions.JInstruction;

public class Main {
    public static void main(String[] args) {
//        int instruction = 0B00000011110000000000000011101111;
//
//        System.out.println(String.format("Loaded instruction %32s",
//                String.format("%32s", Integer.toBinaryString(instruction)).replace(' ', '0')));
//
//        System.out.println(Instruction.Opcode.getOpcode(instruction));
//
//        Decode decode = new Decode(new Registers());
//        decode.input = instruction;
//        decode.currentPC = 64;
//        decode.decode();
//        System.out.println(decode.output);
        
        if (args.length == 0) {
            System.out.println("Must include a file path for an assembly file");
            return;
        }

        Simulator simulator = Simulator.createSimulator(args[0]);
        simulator.runSimulator();
    }
}