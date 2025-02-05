package org.example;

import org.example.processor.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must include a file path for an assembly file");
            return;
        }
        
        Simulator.createSimulator(args[0]);
    }
}