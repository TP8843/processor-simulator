package org.example;

import org.example.processor.data.Registers;
import org.example.processor.instructions.Environment;
import org.example.processor.instructions.IInstructions.EInstructions.EBreakInstruction;
import org.example.processor.instructions.IInstructions.EInstructions.ECallInstruction;

import java.io.IOException;

public class EnvironmentHandler {
    /// To handle register operations
    private final Registers registers;

    /// Whether the program should be halted
    private boolean halted;

    public EnvironmentHandler(Registers registers) {
        this.registers = registers;
    }

    /// Whether the simulator should be halted
    public boolean halted() {
        return halted;
    }

    /// Carries out an environment instruction (halting, printing, etc.)
    public void processEnvironment(Environment instruction) {
        if(instruction instanceof EBreakInstruction){
            // May want to use this more similarly to breakpoints later idk
            halted = true;
        }
        else if(instruction instanceof ECallInstruction i){
            // Check the argument for the type of system call
            switch (registers.getRegister(Registers.A7)) {
                // Also handle this as a kind of halt
                case 0 -> halted = true;

                // Write character to the terminal (putc)
                case 1 -> {
                    System.out.write(registers.getRegister(Registers.A0) & 0xFF);
                }

                // Get character from the terminal (getc)
                case 2 -> {
                    try {
                        registers.setRegister(Registers.A0, System.in.read());
                    } catch (IOException e) {
                        System.out.println("Error: Could not read from console. Writing 0 to A0");
                        registers.setRegister(Registers.A0, 0);
                    }
                }

                // Write integer to the terminal
                case 3 -> System.out.print(registers.getRegister(Registers.A0));
            }
        }
    }
}
