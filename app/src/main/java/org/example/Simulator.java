package org.example;

import org.example.processor.*;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Simulator {
    private final ProgramStore programStore;
    private final Decode decode;
    private final CompareUnit compareUnit;
    private final Alu alu;
    private final Memory memory;
    private final Registers registers;
    private final MemoryAccessor memoryAccessor;
    private final ProgramCountUpdater programCountUpdater;
    private final RegisterWriteBack registerWriteBack;
    
    private int currentStage = 0;
    private int cycles;
    
    // True if program should continue on its own
    private boolean autoRun = false;
    
    private final String printError = """
            Possible Commands:
                programStore/program-store                - Stores loaded program (current instruction)
                decode                                    - Decode Unit
                compareUnit/compare-unit                  - Compare Unit (handles = and != for branching)
                alu                                       - ALU (handles general computation)
                memoryAccessor/memory-accessor            - Handles memory writes/reads
                programCountUpdater/program-count-updater - Handles updating PC (incrementing or setting on branch)
                registerWriteBack/register-write-back     - Handles writing back to the registers
            """;
    
    private final String generalError = """
            Possible Commands:
                print \\033[3mcomponent\\033[0m - prints current state of component
                step                - runs next line of assembly
                continue            - runs all of program without breaks
            """;
    
    Simulator(ProgramStore programStore, 
              Decode decode,
              CompareUnit compareUnit,
              Alu alu,
              MemoryAccessor memoryAccessor,
              Memory memory,
              Registers registers,
              ProgramCountUpdater programCountUpdater,
              RegisterWriteBack registerWriteBack) {
        this.programStore = programStore;
        this.decode = decode;
        this.compareUnit = compareUnit;
        this.alu = alu;
        this.memory = memory;
        this.registers = registers;
        this.memoryAccessor = memoryAccessor;
        this.programCountUpdater = programCountUpdater;
        this.registerWriteBack = registerWriteBack;
        cycles = 0;
    }
    
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (!programStore.getHalted()) {
            if (!autoRun) {
                System.out.print("> ");
                String line = scanner. nextLine();
                line = line.toLowerCase().trim();
                
                // Print a unit's contents if starts with print
                if (line.startsWith("print"))
                    processPrint(line.substring("print".length()).trim());
                else if (line.startsWith("step"))
                    runCycle();
                else if (line.startsWith("continue")) {
                    autoRun = true;
                    runCycle();
                } else {
                    System.out.println("Unknown command: " + line);
                    System.out.print(generalError);
                }
            } else {
                runCycle();
            }
        }
    }
    
    private void processPrint(String unit) {
        switch (unit) {
            case "alu": System.out.println(alu);
            case "memory": System.out.println(memory);
            case "registers": System.out.println(registers);
            case "memoryAccessor", "memory-accessor": System.out.println(memoryAccessor);
            case "programCountUpdater", "program-count-updater": System.out.println(programCountUpdater);
            case "registerWriteBack", "register-write-back": System.out.println(registerWriteBack);
            case "compareUnit", "compare-unit": System.out.println(compareUnit);
            case "decode": System.out.println(decode);
            case "programStore", "program-store": System.out.println(programStore);
            default: {
                System.out.println("Unknown command: " + unit);
                System.out.print(printError);
            }
        }
    }

    private void runCycle(){
        switch (currentStage) {
            case 0 -> {
                programStore.getInstruction();
                cycles += 1;
            }
            case 1 -> {
                decode.decode();
                cycles += 1;
            }
            case 2 -> {
                alu.execute();
                compareUnit.process();
                cycles += 1;
            }
            case 3 -> {
                memoryAccessor.processInstruction();
                programCountUpdater.process();
                cycles += 1;
            }
            case 4 -> {
                registerWriteBack.processWriteBack();
                cycles += 1;
            }
        }

        currentStage = (currentStage + 1) % 5;
    }
}
