package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.Instruction;

public class IssueUnit {
    private final Registers registers;
    
    public final Buffer<Instruction> decodeIssueBuffer;
    
    public final Buffer<Instruction> aluReservationStation;
    
    /// Issues an instruction from the decode issue buffer to the correct
    /// reservation station
    public void issue(){
        if(!decodeIssueBuffer.hasValue()) return;
        
        var instruction = decodeIssueBuffer.peek().get();

        // Add data if available, and if data is not available (returned instruction has not got data) output = null
        instruction.addDataIfAvailable(registers);
        
        switch (instruction.getEU()) {
            case ALU -> {
                if(aluReservationStation.hasSpace())
                    aluReservationStation.put(decodeIssueBuffer.pop().get());
            }
            case COMPARE -> {
                decodeIssueBuffer.pop();
            }
            default -> {}
        }
    }
    
    public IssueUnit(Registers registers,
                     Buffer<Instruction> decodeIssueBuffer, 
                     Buffer<Instruction> aluReservationStation) {
        this.registers = registers;
        this.decodeIssueBuffer = decodeIssueBuffer;
        this.aluReservationStation = aluReservationStation;
    }
}
