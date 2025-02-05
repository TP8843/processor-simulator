package org.example.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {
    /// Number of words in memory
    public final int size = 2048;
    
    /// Number of bytes in word of memory
    public final int wordLength = 4;
    
    private final int[] memory = new int[size];
    
    public int getWord(int pos) {
        if (pos % wordLength != 0) throw new IllegalArgumentException("Memory access is not word aligned");
        
        return memory[pos / wordLength];
    }
    
    public int getHalfWord(int pos, boolean unsigned) {
        if (pos % (wordLength / 2) != 0) throw new IllegalArgumentException("Memory access is not half word aligned");
        
        int value = (memory[pos / wordLength] >> ((pos % wordLength) * 8) & 0b1111111111111111);
        
        if (unsigned) {
            return value;
        } else {
            return (value << 16) >> 16;
        }
    }

    public int getByte(int pos, boolean unsigned) {
        int value = (memory[pos / wordLength] >> ((pos % wordLength) * 8) & 0b11111111);
        
        if (unsigned) {
            return value;
        } else {
            return (value << 16) >> 16;
        }
    }
    
    public void storeWord(int pos, int input) {
        if (pos % wordLength != 0) throw new IllegalArgumentException("Memory access is not word aligned");
        
        memory[pos / wordLength] = input;
    }

    public void storeHalfWord(int pos, int input) {
        if (pos % (wordLength / 2) != 0) throw new IllegalArgumentException("Memory access is not half word aligned");

        memory[pos / wordLength] = memory[pos / wordLength] | ((input & 0xFF) << (((pos % wordLength) / 2) * 16));
    }

    public void storeByte(int pos, int input) {
        memory[pos / wordLength] = memory[pos / wordLength] | ((input & 0xF) << ((pos % wordLength) * 8));
    }

    /// Load program into memory, starting at 0
    public void loadProgram(String filename) {
        try(Scanner scanner = new Scanner(new File(filename))) {
            int lineCount = 0;

            while(scanner.hasNextInt()) {
                int instruction = scanner.nextInt();

                memory[lineCount] = instruction;
                lineCount += 1;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
