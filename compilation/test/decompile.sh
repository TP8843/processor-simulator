#!/bin/bash

FILE_PATH="$1"
if [ ! -f "$FILE_PATH" ]; then
    echo "File '$FILE_PATH' not found"
    exit 1
fi

DIR=$(dirname "$FILE_PATH")
FILE=$(basename "$FILE_PATH")
FILENAME=$(echo "$FILE" | cut -d. -f1)
OUTPUT="$DIR/$FILENAME.txt"

riscv64-unknown-elf-objdump -d $FILE > $OUTPUT