package org.example;

import java.util.HashSet;
import java.util.HexFormat;
import java.util.Scanner;
import java.util.Set;

// TODO: Update all prints to allow for printing buffers and swag

public class Debugger {
    private final Simulator simulator;

    private final String printError = """
            Possible Commands:
                instruction-fetch/instructionFetch/fetch  - Instruction Fetch Unit
                fetch-decode/fetchDecode                  - Fetch to Decode Buffer
                decode-branch/decodeBranch                - Decode to Branch Buffer
                decode-issue/decodeIssue                  - Decode to Issue Buffer
            
                alu-rs/aluRs                              - ALU Reservation Station
                agu-rs/aguRs                              - AGU Reservation Station
                compare-rs/compareRs                      - Compare Reservation Station
            
                agu-memory/aguMemory                      - AGU to Memory Load Unit Buffer
            
                rob                                       - Reorder Buffer
                registers                                 - Registers
                memory                                    - Memory
                state                                     - Current State of Simulation
                
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

    private final Set<Integer> breakPoints = new HashSet<>();
    
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
                do {
                    runProcessorCycle();
                } while (!simulator.decode.getHalted() && !breakPoints.contains(simulator.instructionFetch.getPC() - 4));
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
            case "instruction-fetch", "fetch", "instructionfetch": System.out.println(simulator.instructionFetch); break;
            case "fetch-decode", "fetchdecode": System.out.println(simulator.fetchDecodeBuffer); break;
            case "decode-branch", "decodebranch": System.out.println(simulator.decodeBranchBuffer); break;
            case "decode-issue", "decodeissue": System.out.println(simulator.decodeIssueBuffer); break;

            case "alu-rs", "alurs": System.out.println(simulator.aluReservationStation); break;
            case "agu-rs", "agus": System.out.println(simulator.aguReservationStation); break;
            case "compare-rs", "comparers": System.out.println(simulator.compareReservationStation); break;

            case "agu-memory", "agumemory": System.out.println(simulator.aguLoadBuffer); break;

            case "rob": System.out.println(simulator.rob); break;
            case "registers": System.out.println(simulator.registers); break;
            case "memory": System.out.println(simulator.memory); break;
            case "state": printState(); break;

            default: {
                System.out.println("Unknown command: " + unit);
                System.out.println(printError);
                break;
            }
        }
    }

    private void printState() {
        System.out.println(
                        String.format("""
                            Current PC: 0x%s
                            Current Cycle Count: %s
                            Current Instructions per Cycle: %s
                            Current Cycles per Instruction: %s
                            Halted: %s""", 
                        Integer.toHexString(simulator.instructionFetch.getPC()), 
                        cycles, 
                        (float) simulator.getInstructions() / (float) cycles,
                        (float) cycles / (float) simulator.getInstructions(),
                        simulator.decode.getHalted() ? "True" : "False"));
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
