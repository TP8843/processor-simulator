# Cycle Steps

## 1. Instruction Fetch Cycle (IF)
- Send program counter (PC) to memory
- Fetch current instruction from memory
- Add 1 to current program counter

## 2. Instruction Decode / Register Fetch Cycle
- Decode the instruction
- Read the registers corresponding to register source specifiers from the register file
- If smart, branch updates PC here

## 3. Execution / Effective Address Cycle
- ALU operates on operands prepared in the prior cycle
- **Memory reference** - ALU adds base register to the offset to form the effective address
- **Register-Register ALU instruction** - The ALU performs the operation according to instruction opcode on values read from register file
- **Register-Immediate ALU instruction** - The ALU performs the operation according to instruction opcode on first value read from register, and the immediate value (would be sign-extended in actual implementation)

## 4. Memory Access Cycle
- If the instruction is a load, the memory does a read using the effective address computed in the previous cycle
- If the instruction is a store, then the memory writes the data from the second register read from the register file using the effective address

## 5. Write-Back Cycle
- For:
  - Register-Register ALU instructions
  - Load instructions
- Write the result into the register file, either from memory (for load instruction), or from ALU (for ALU instruction)