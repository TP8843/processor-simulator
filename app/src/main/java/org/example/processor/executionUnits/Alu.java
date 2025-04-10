package org.example.processor.executionUnits;

import org.example.processor.buffers.Buffer;
import org.example.processor.instructions.*;
import org.example.processor.instructions.IInstructions.*;
import org.example.processor.instructions.IInstructions.EInstructions.EBreakInstruction;
import org.example.processor.instructions.IInstructions.EInstructions.ECallInstruction;
import org.example.processor.instructions.IInstructions.LoadInstructions.*;
import org.example.processor.instructions.JInstructions.JALInstruction;
import org.example.processor.instructions.RInstructions.*;
import org.example.processor.instructions.SInstructions.SInstruction;
import org.example.processor.instructions.UInstructions.AUIInstruction;
import org.example.processor.instructions.UInstructions.LUIInstruction;

import java.util.Optional;

public record Alu(Buffer<Instruction> input) {

    public void execute() {
        Optional<Instruction> value = input.pop();
        if(value.isEmpty()) return;

        Instruction instruction = value.get();

        switch (instruction) {
            case LBInstruction i -> i.addResult(i.rs1.getData() + i.imm);
            case LBUInstruction i -> i.addResult(i.rs1.getData() + i.imm);
            case LHWInstruction i -> i.addResult(i.rs1.getData() + i.imm);
            case LHWUInstruction i -> i.addResult(i.rs1.getData() + i.imm);
            case LWInstruction i -> i.addResult(i.rs1.getData() + i.imm);

            case SLTIInstruction i -> i.addResult(i.rs1.getData() < i.imm ? 1 : 0);
            case SLTIUInstruction i -> i.addResult(Integer.compareUnsigned(i.rs1.getData(), i.imm) < 0 ? 1 : 0);

            case AddIInstruction i -> i.addResult(i.rs1.getData() + i.imm);

            case ANDIInstruction i -> i.addResult(i.rs1.getData() & i.imm);
            case ORIInstruction i -> i.addResult(i.rs1.getData() | i.imm);
            case XORIInstruction i -> i.addResult(i.rs1.getData() ^ i.imm);

            case SLLIInstruction i -> i.addResult(i.rs1.getData() << (i.imm & 0b11111));
            case SRLIInstruction i -> i.addResult(i.rs1.getData() >>> (i.imm & 0b11111));
            case SRAIInstruction i -> i.addResult(i.rs1.getData() >> (i.imm & 0b11111));

            case ECallInstruction i -> {
            }
            case EBreakInstruction i -> {
            }

            case JALInstruction i -> i.addResult(i.getPC() + 4);
            case JALRInstruction i -> i.addResult(i.getPC() + 4);

            case ADDInstruction i -> i.addResult(i.rs1.getData() + i.rs2.getData());
            case SUBInstruction i -> i.addResult(i.rs1.getData() - i.rs2.getData());
            case ORInstruction i -> i.addResult(i.rs1.getData() | i.rs2.getData());
            case ANDInstruction i -> i.addResult(i.rs1.getData() & i.rs2.getData());
            case XORInstruction i -> i.addResult(i.rs1.getData() ^ i.rs2.getData());

            case SLLInstruction i -> i.addResult(i.rs1.getData() << (i.rs2.getData() & 0b11111));
            case SRLInstruction i -> i.addResult(i.rs1.getData() >>> (i.rs2.getData() & 0b11111));
            case SRAInstruction i -> i.addResult(i.rs1.getData() >> (i.rs2.getData() & 0b11111));

            case SLTInstruction i -> i.addResult((i.rs1.getData() < i.rs2.getData()) ? 1 : 0);
            case SLTUInstruction i -> i.addResult(Integer.compareUnsigned(i.rs1.getData(), i.rs2.getData()) < 0 ? 1 : 0);

            case SInstruction i -> i.addAddress(i.rs1.getData() + i.imm);

            case LUIInstruction i -> i.addResult(i.imm);
            case AUIInstruction i -> i.addResult(i.imm + i.getPC());

            default -> throw new IllegalArgumentException("Instruction not valid for ALU: " + instruction);
        }
    }
}
