package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.commit.ROB;
import org.example.processor.data.Registers;
import org.example.processor.instructions.Instruction;

public class IssueUnit {
    private final Registers registers;
    
    public final Buffer<Instruction> decodeIssueBuffer;
    
    public final Buffer<Instruction> aluReservationStation;
    public final Buffer<Instruction> compareReservationStation;
    public final Buffer<Instruction> aguReservationStation;

    /// ROB so that instructions can be added immediately on issue
    public final ROB rob;
    
    /// Issues an instruction from the decode issue buffer to the correct
    /// reservation station
    public void issue(){
        if(!decodeIssueBuffer.hasValue() || rob.isFull()) return;
        
        var instruction = decodeIssueBuffer.peek().get();
        
        switch (instruction.getEU()) {
            case ALU -> {
                if(aluReservationStation.hasSpace()){
                    // Add instruction to ROB before adding to RS
                    rob.add(instruction);
                    aluReservationStation.put(decodeIssueBuffer.pop().get());
                }
            }
            case COMPARE -> {
                if(compareReservationStation.hasSpace()){
                    // Add instruction to ROB before adding to RS
                    rob.add(instruction);
                    compareReservationStation.put(decodeIssueBuffer.pop().get());
                }
            }
            case AGU -> {
                if(aguReservationStation.hasSpace()){
                    // Add instruction to ROB before adding to RS
                    rob.add(instruction);
                    aguReservationStation.put(decodeIssueBuffer.pop().get());
                }
            }

            case NONE -> {
                rob.add(instruction);
            }

            default -> {}
        }
    }
    
    public IssueUnit(Registers registers,
                     Buffer<Instruction> decodeIssueBuffer, 
                     Buffer<Instruction> aluReservationStation,
                     Buffer<Instruction> compareReservationStation,
                     Buffer<Instruction> aguReservationStation,
                     ROB rob) {
        this.registers = registers;
        this.decodeIssueBuffer = decodeIssueBuffer;
        this.aluReservationStation = aluReservationStation;
        this.compareReservationStation = compareReservationStation;
        this.aguReservationStation = aguReservationStation;
        this.rob = rob;
    }
}
