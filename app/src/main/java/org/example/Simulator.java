package org.example;

import org.example.processor.*;
import org.example.processor.instructions.*;

import java.util.*;

public class Simulator {
    private final Memory memory;
    private final Registers registers;
    private final InstructionFetch instructionFetch;
    private final Decode decode;
    private final Alu alu;
    private final CompareUnit compareUnit;
    private final BranchUnit branchUnit;
    private final MemoryAccessUnit memoryAccessUnit;
    private final WriteBackUnit writeBackUnit;

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

    private int stage = 0;
    private int cycles = 0;

    public Simulator(Memory memory,
                     Registers registers,
                     InstructionFetch instructionFetch,
                     Decode decode,
                     Alu alu,
                     CompareUnit compareUnit,
                     BranchUnit branchUnit,
                     MemoryAccessUnit memoryAccessUnit,
                     WriteBackUnit writeBackUnit) {
        this.memory = memory;
        this.registers = registers;
        this.instructionFetch = instructionFetch;
        this.decode = decode;
        this.alu = alu;
        this.compareUnit = compareUnit;
        this.branchUnit = branchUnit;
        this.memoryAccessUnit = memoryAccessUnit;
        this.writeBackUnit = writeBackUnit;
    }

    public void runSimulator() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            line = line.toLowerCase().trim();

            // Print a unit's contents if starts with print
            if (line.startsWith("print"))
                processPrint(line.substring("print".length()).trim());
            else if (line.startsWith("step")) {
                runCycle();
                printState();
            } else if (line.startsWith("continue")) {
                while (!alu.isHalted() && !breakPoints.contains(instructionFetch.getPC() - 4)) {
                    runCycle();
                }
            } else if (line.startsWith("exit")) {
                System.out.println("Final value: " + memory.getWord(0));
                return;
            } else if (line.startsWith("breakpoint")) {
                processBreakpoint(line.substring("breakpoint".length()).trim());
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
            case "memoryaccessor", "memory-accessor": System.out.println(memoryAccessUnit); break;
            case "branchunit", "branch-unit", "branch": System.out.println(branchUnit); break;
            case "writebackunit", "write-back-unit", "writeback", "write-back": System.out.println(writeBackUnit); break;
            case "compareunit", "compare-unit", "compare": System.out.println(compareUnit); break;
            case "decode-unit", "decodeunit", "decode": System.out.println(decode); break;
            case "instruction-fetch", "instructionfetch": System.out.println(instructionFetch); break;
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
                        Current PC: 0x%s
                        Current Cycle Count: %s""", stage, Integer.toHexString(instructionFetch.getPC()), cycles));
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

    private void runCycle() {
        switch (stage){
            case 0 -> {
                instructionFetch.process();
                decode.input = instructionFetch.output;
                decode.currentPC = instructionFetch.getPC() - 4;
                
                cycles += 1;
            }
            case 1 -> {
                decode.decode();
                alu.input = decode.output;
                compareUnit.input = decode.output;
                
                cycles += 1;
            }
            case 2 -> {
                alu.execute();
                compareUnit.execute();
                branchUnit.compareInput = compareUnit.output;
                branchUnit.aluInput = alu.output;
                memoryAccessUnit.input = alu.output;

                cycles += 1;
            }
            case 3 -> {
                memoryAccessUnit.process();
                branchUnit.updatePC();
                writeBackUnit.input = memoryAccessUnit.output;

                cycles += 1;
            }
            case 4 -> {
                writeBackUnit.writeBack();

                cycles += 1;
                
                System.out.println("Finished processing instruction " + Integer.toHexString(instructionFetch.getPC() - 4));
            }
        }
        stage = (stage + 1) % 5;
    }

    static public Simulator createSimulator(String fileName) {
        Memory memory = new Memory();
        Registers registers = new Registers();
        InstructionFetch instructionFetch = new InstructionFetch(memory, 8);
        Decode decode = new Decode(registers);
        Alu alu = new Alu();
        CompareUnit compareUnit = new CompareUnit();
        BranchUnit branchUnit = new BranchUnit(instructionFetch);
        MemoryAccessUnit memoryAccessUnit = new MemoryAccessUnit(memory);
        WriteBackUnit writeBackUnit = new WriteBackUnit(registers);

        memory.loadProgram(fileName, 8);

        return new Simulator(
                memory,
                registers,
                instructionFetch,
                decode,
                alu,
                compareUnit,
                branchUnit,
                memoryAccessUnit,
                writeBackUnit
        );
    }
}
