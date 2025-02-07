#!/bin/bash

FILE_PATH="$1"
if [ ! -f "$FILE_PATH" ]; then
    echo "File '$FILE_PATH' not found"
    exit 1
fi

DIR=$(dirname "$FILE_PATH")
FILE=$(basename "$FILE_PATH")
FILENAME=$(echo "$FILE" | cut -d. -f1)
ELF="$DIR/$FILENAME.out"
BIN="$DIR/$FILENAME.bin"

echo "Compiling $FILE..."

# To see the linker script, add the `-Wl,--verbose` option
if riscv64-unknown-elf-gcc -mabi=ilp32 -march=rv32i -no-pie -nostdlib -Wl,-T,link.ld -e main -O0 -o "$ELF" crt0.s "$FILE_PATH" ; then
    echo "Finished compilation of ELF to $ELF"
    riscv64-unknown-elf-objcopy -O binary $ELF $BIN
    echo "Finished binary dump to $BIN"
else 
  echo "Failed compilation of $FILE"
fi

echo "Done"
