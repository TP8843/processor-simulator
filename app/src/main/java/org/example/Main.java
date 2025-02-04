package org.example;

import org.example.processor.*;

public class Main {
    private static void startSimulator(String path){
        System.out.println("Running program from " + path);
        
        
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Must include a file path for an assembly file");
            return;
        }
        
        startSimulator(args[0]);
    }
}