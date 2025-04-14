package org.example.processor;

import org.example.processor.buffers.Buffer;
import org.example.processor.commit.ROB;
import org.example.processor.data.Registers;
import org.example.processor.instructions.Instruction;

import java.util.Optional;

public class IssueUnit {
    public final Buffer<Instruction> decodeIssueBuffer;
    
    public final Buffer<Instruction> aluReservationStation;
    public final Buffer<Instruction> compareReservationStation;
    public final Buffer<Instruction> aguReservationStation;
    public final Buffer<Instruction> multiplyReservationStation;

    /// ROB so that instructions can be added immediately on issue
    public final ROB rob;
    
    /// Issues an instruction from the decode issue buffer to the correct
    /// reservation station
    public void issue(){
        if(!decodeIssueBuffer.hasValue() || rob.isFull()) return;

        Optional<Instruction> value = decodeIssueBuffer.peek();
        if(value.isEmpty()) return;
        var instruction = value.get();
        
        switch (instruction.getEU()) {
            case ALU -> {
                if(aluReservationStation.hasSpace()){
                    // Add instruction to ROB before adding to RS
                    rob.add(instruction);
                    decodeIssueBuffer.pop();
                    aluReservationStation.put(instruction);
                }
            }
            case COMPARE -> {
                if(compareReservationStation.hasSpace()){
                    // Add instruction to ROB before adding to RS
                    rob.add(instruction);
                    decodeIssueBuffer.pop();
                    compareReservationStation.put(instruction);
                }
            }
            case AGU -> {
                if(aguReservationStation.hasSpace()){
                    // Add instruction to ROB before adding to RS
                    rob.add(instruction);
                    decodeIssueBuffer.pop();
                    aguReservationStation.put(instruction);
                }
            }
            case MULTIPLY -> {
                if(multiplyReservationStation.hasSpace()){
                    rob.add(instruction);
                    decodeIssueBuffer.pop();
                    multiplyReservationStation.put(instruction);
                }
            }

            case NONE -> {
                rob.add(instruction);
            }

            default -> {}
        }
    }
    
    public IssueUnit(Buffer<Instruction> decodeIssueBuffer,
                     Buffer<Instruction> aluReservationStation,
                     Buffer<Instruction> compareReservationStation,
                     Buffer<Instruction> aguReservationStation,
                     Buffer<Instruction> multiplyReservationStation,
                     ROB rob) {
        this.decodeIssueBuffer = decodeIssueBuffer;
        this.aluReservationStation = aluReservationStation;
        this.compareReservationStation = compareReservationStation;
        this.aguReservationStation = aguReservationStation;
        this.multiplyReservationStation = multiplyReservationStation;
        this.rob = rob;
    }
}
