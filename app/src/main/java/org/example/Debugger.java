package org.example;

import java.util.HashSet;
import java.util.HexFormat;
import java.util.Scanner;
import java.util.Set;

public class Debugger {
    private final Simulator simulator;

    private final String printError = """
            Possible Commands:
                decode/decode-unit/decodeUnit             - Decode Unit
                compareUnit/compare-unit/compare          - Compare Unit (handles = and != for branching)
                alu                                       - ALU (handles general computation)
                memoryAccessor/memory-accessor            - Handles memory writes/reads
                branchUnit/program-count-updater          - Handles updating PC (incrementing or setting on branch)
                writeBackUnit/write-back-unit             - Handles writing back to the registers
                memory                                    - Memory storing data and program
                registers                                 - Current value of all registers
            """;

    private final String generalError = """
            Possible Commands:
                print *component*    - prints current state of component
                step                 - runs next line of assembly
                continue             - runs all of program, only stopping at breakpoints
                breakpoint *command* - adds, removes, and lists breakpoints (type breakpoint for help)
            """;

    private final String breakpointError = """
            Possible Commands:
                add *breakpoint*    - adds breakpoint to line
                remove *breakpoint* - removes breakpoint for line
                list                - lists all current breakpoints
            """;

    private Set<Integer> breakPoints = new HashSet<>();
    
    private int cycles = 0;
    
    public Debugger(Simulator simulator) {
        this.simulator = simulator;
    }

    /// Runs debugger with the simulator
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            line = line.toLowerCase().trim();

            // Print a unit's contents if starts with print
            if (line.startsWith("print"))
                processPrint(line.substring("print".length()).trim());
            else if (line.startsWith("step")) {
                runProcessorCycle();
                printState();
            } else if (line.startsWith("continue")) {
                while (!simulator.alu.isHalted() && !breakPoints.contains(simulator.instructionFetch.getPC() - 4)) {
                    runProcessorCycle();
                }
            } else if (line.startsWith("exit")) {
                System.out.println("Final value: " + simulator.memory.getWord(0));
                return;
            } else if (line.startsWith("breakpoint")) {
                processBreakpoint(line.substring("breakpoint".length()).trim());
            } else {
                System.out.println("Unknown command: " + line);
                System.out.print(generalError);
            }
        }
    }
    
    private void runProcessorCycle() {
        simulator.runCycle();
        cycles += 1;
    }

    private void processPrint(String unit) {
        switch (unit) {
            case "alu": System.out.println(simulator.alu); break;
            case "memory": System.out.println(simulator.memory); break;
            case "registers": System.out.println(simulator.registers); break;
            case "memoryaccessor", "memory-accessor": System.out.println(simulator.memoryAccessUnit); break;
            case "branchunit", "branch-unit", "branch": System.out.println(simulator.branchUnit); break;
            case "writebackunit", "write-back-unit", "writeback", "write-back": System.out.println(simulator.writeBackUnit); break;
            case "compareunit", "compare-unit", "compare": System.out.println(simulator.compareUnit); break;
            case "decode-unit", "decodeunit", "decode": System.out.println(simulator.decode); break;
            case "instruction-fetch", "instructionfetch": System.out.println(simulator.instructionFetch); break;
            case "state": printState(); break;
            default: {
                System.out.println("Unknown command: " + unit);
                System.out.print(printError);
                break;
            }
        }
    }

    private void printState() {
        System.out.println(
                        String.format("""
                            Current Stage: %s
                            Current PC: 0x%s
                            Current Cycle Count: %s
                            Halted: %s""", simulator.getStage(), 
                        Integer.toHexString(simulator.instructionFetch.getPC()), 
                        cycles,
                        simulator.alu.isHalted() ? "True" : "False"));
    }

    private void processBreakpoint(String command){
        if(command.startsWith("add"))
            breakPoints.add(HexFormat.fromHexDigits(command.substring("add".length()).trim()));
        else if(command.startsWith("remove"))
            breakPoints.remove(HexFormat.fromHexDigits(command.substring("remove".length()).trim()));
        else if(command.startsWith("list"))
            System.out.println(breakPoints);
        else {
            System.out.println("Unknown command: " + command);
            System.out.print(breakpointError);
        }
    }
}
