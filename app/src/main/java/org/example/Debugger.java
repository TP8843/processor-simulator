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
                fetchdecode/fetch-decode                  - Fetch to decode buffer
                decode-branch/decodebranch                - Decode to branch buffer
                decode-issue/decodeissue                  - Decode to issue buffer
                branch/branchunit/branch-unit             - Branch Unit
                alumemory/alu-memory                      - ALU to Memory Access Unit Buffer
                alurs/alu-rs                              - ALU Reservation Station
                comparers/compare-rs                      - Compare Reservation Station
                alu                                       - ALU state (currently just if halted)
                comparebranch/compare-branch              - Compare Unit to Branch Unit Buffer
                memorywriteback/memory-write-back         - Memory Access Unit to Write Back Unit Buffer
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
                do {
                    runProcessorCycle();
                } while (!simulator.branchUnit.getEndReached() && !breakPoints.contains(simulator.instructionFetch.getPC() - 4));
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
            case "alumemory", "alu-memory": System.out.println(simulator.aluMemoryBuffer); break;
            case "comparebranch", "compare-branch": System.out.println(simulator.compareBranchBuffer); break;
            case "alurs", "alu-rs": System.out.println(simulator.aluReservationStation); break;
            case "comparers", "compare-rs": System.out.println(simulator.compareReservationStation); break;
            case "memorywriteback", "memory-write-back", "memory-writeback": System.out.println(simulator.memoryWriteBackBuffer); break;
            case "decodeissuebuffer", "decode-issue-buffer", "decode-issue", "decodeissue": System.out.println(simulator.decodeIssueBuffer); break;
            case "decodebranchbuffer", "decode-branch-buffer", "decode-branch", "decodebranch": System.out.println(simulator.decodeBranchBuffer); break;
            case "fetchdecode", "fetch-decode": System.out.println(simulator.fetchDecodeBuffer); break;
            case "branch", "branchunit", "branch-unit": System.out.println(simulator.branchUnit); break;
            case "instruction-fetch", "instructionfetch", "fetch": System.out.println(simulator.instructionFetch); break;
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
                            Current PC: 0x%s
                            Current Cycle Count: %s
                            Current Instructions per Cycle: %s
                            Current Cycles per Instruction: %s
                            Halted: %s""", 
                        Integer.toHexString(simulator.instructionFetch.getPC()), 
                        cycles, 
                        (float) simulator.getInstructions() / (float) cycles,
                        (float) cycles / (float) simulator.getInstructions(),
                        simulator.branchUnit.getEndReached() ? "True" : "False"));
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
