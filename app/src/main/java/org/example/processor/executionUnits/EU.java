package org.example.processor.executionUnits;

/// All possible execution units to send an instruction
public enum EU {
    /// Integer Operations
    ALU,

    /// Comparisons for Branches
    COMPARE,

    /// Address Generation Unit (for loads/stores)
    AGU,

    /// No Processing Required (Mainly for Environment)
    NONE
}
