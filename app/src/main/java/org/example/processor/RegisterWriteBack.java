package org.example.processor;

public class RegisterWriteBack {
    public Instruction input;
    private final Registers registers;
    
    public RegisterWriteBack(Registers registers) {
        this.registers = registers;
    }
    
    public void processWriteBack() {
        switch (input.opcode) {
            case ADD, SUB, ADDI, SUBI, MUL, DIV, AND, ANDI, OR, ORI, XOR, XORI, SLT, SLTI -> {
                registers.setValue(input.writeBackAddress, input.output);
            }
        }
    }
}
