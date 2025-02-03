package org.example.processor;

public class RegisterWriteBack {
    public DecodedInstruction input;
    private final Registers registers;
    
    public RegisterWriteBack(Registers registers) {
        this.registers = registers;
    }
    
    public void processWriteBack() {
        switch (input.opcode) {
            case ADD, SUB, ADDI, SUBI, MUL, DIV, AND, ANDI, OR, ORI, XOR, XORI, SLT, SLTI -> {
                registers.setValue(input.destination, input.aluOutput);
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("""
                Register Write Back:
                    Input: %s   
                """, input);
    }
}
