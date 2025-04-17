// Print character to terminal
void printc(char character) {
    // Loads number for putc
    register int call asm("a7") = 1;
    register char value asm("a0") = character;

    asm volatile("ecall" : : "r"(call), "r"(value));
}

void printInt(int number) {
    register int call asm("a7") = 3;
    register int value asm("a0") = number;

    asm volatile("ecall" : : "r"(call), "r"(value));
}

// Prints null terminated string to terminal (be careful)
void print(const char* string) {
    int i = 0;
    while (string[i] != '\0') {
        printc(string[i]);
        i += 1;
    }
}

void printIntArray(int* array, int count) {
    for(int i = 0; i < count - 1; i++) {
        printInt(array[i]);
        print(", ");
    }
    printInt(array[count - 1]);
}