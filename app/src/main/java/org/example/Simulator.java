package org.example;

import org.example.processor.*;
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
        while (true) {
            System.out.print("> ");
            String line = scanner. nextLine();
            line = line.toLowerCase().trim();
            
            // Print a unit's contents if starts with print
            if (line.startsWith("print"))
                processPrint(line.substring("print".length()).trim());
            else if (line.startsWith("step")) {
                runCycle();
                printState();
            } else if (line.startsWith("continue")) {
                while (!programStore.getHalted()) {
                    runCycle();
                }
            } else if (line.startsWith("exit")) {
                return;
            } else {
                System.out.println("Unknown command: " + line);
                System.out.print(generalError);
            }
        }
    }
    
    private void processPrint(String unit) {
        switch (unit) {
            case "alu": System.out.println(alu); break;
            case "memory": System.out.println(memory); break;
            case "registers": System.out.println(registers); break;
            case "memoryaccessor", "memory-accessor": System.out.println(memoryAccessor); break;
            case "programcountupdater", "program-count-updater": System.out.println(programCountUpdater); break;
            case "registerwriteback", "register-write-back": System.out.println(registerWriteBack); break;
            case "compareunit", "compare-unit": System.out.println(compareUnit); break;
            case "decode": System.out.println(decode); break;
            case "programstore", "program-store": System.out.println(programStore); break;
            case "state": printState(); break;
            default: {
                System.out.println("Unknown command: " + unit);
                System.out.print(printError);
                break;
            }
        }
    }
    
    private void printState() {
        System.out.println(String.format("""
                        Current Stage: %s
                        Current PC: %s
                        Current Cycle Count: %s""", currentStage, programCountUpdater.getCurrentPC(), cycles));
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
