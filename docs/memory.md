# Memory Structure
- In RISCV, memory is byte addressed.
- In simulator, memory stored as int array, with each int a word.
- Simulator currently uses a Von Neumann memory architecture.

## Load / Store
- Load Word
- Load Half Word
- Load Byte
- Store Word
- Store Half Word
- Store Byte

## Instructions
- Stored in the same memory as the data
- Loaded from a .bin (binary) file encoded in little endian

## Special Memory Locations
- **0**: Outputted at end of execution of the program.