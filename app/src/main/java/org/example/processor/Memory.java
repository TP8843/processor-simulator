package org.example.processor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class Memory {
    /// Number of words in memory
    public final int size = 6 * 1024 + 10;
    
    /// Number of bytes in word of memory
    public final int wordLength = 4;
    
    private final int[] memory = new int[size /wordLength];
    
    private String previousStore = "None";
    private String previousLoad = "None";
    
    public int getWord(int pos) {
        if (pos % wordLength != 0) throw new IllegalArgumentException("Memory access is not word aligned");
        
        final int output = memory[pos / wordLength];
        
        previousLoad = String.format("Word loaded from byte %s, array location %s, value %s",
                pos, pos / wordLength, output);
        
        return output;
    }
    
    public int getHalfWord(int pos, boolean unsigned) {
        if (pos % (wordLength / 2) != 0) throw new IllegalArgumentException("Memory access is not half word aligned");
        
        int value = (memory[pos / wordLength] >> ((pos % wordLength) * 8) & 0b1111111111111111);
        
        if (unsigned) {
            previousLoad = String.format("Unsigned half word loaded from byte %s, array location %s, value %s",
                    pos, pos / wordLength, value);
            return value;
        } else {
            previousLoad = String.format("Signed half word loaded from byte %s, array location %s, value %s",
                    pos, pos / wordLength, (value << 16) >> 16);
            return (value << 16) >> 16;
        }
    }

    public int getByte(int pos, boolean unsigned) {
        int value = (memory[pos / wordLength] >> ((pos % wordLength) * 8) & 0b11111111);
        
        if (unsigned) {
            previousLoad = String.format("Unsigned byte loaded from byte %s, array location %s, value %s",
                    pos, pos / wordLength, value);
            return value;
        } else {
            previousLoad = String.format("Signed byte loaded from byte %s, array location %s, value %s",
                    pos, pos / wordLength, (value << 16) >> 16);
            return (value << 16) >> 16;
        }
    }
    
    public void storeWord(int pos, int input) {
        if (pos % wordLength != 0) throw new IllegalArgumentException("Memory access is not word aligned");
        
        memory[pos / wordLength] = input;

        previousStore = String.format("Word stored into byte %s, array location %s, value %s",
                pos, pos / wordLength, input);

        System.out.println(previousStore);
    }

    public void storeHalfWord(int pos, int input) {
        if (pos % (wordLength / 2) != 0) throw new IllegalArgumentException("Memory access is not half word aligned");

        final int transformedInput = ((input & 0xFF) << (((pos % wordLength) / 2) * 16));
        
        memory[pos / wordLength] = memory[pos / wordLength] | transformedInput;

        previousStore = String.format("Half word stored into byte %s, array location %s, value %s",
                pos, pos / wordLength, transformedInput);

        System.out.println(previousStore);
    }

    public void storeByte(int pos, int input) {
        final int transformedInput = ((input & 0xF) << ((pos % wordLength) * 8));
        
        memory[pos / wordLength] = memory[pos / wordLength] | transformedInput;

        previousStore = String.format("Byte stored into byte %s, array location %s, value %s",
                pos, pos / wordLength, transformedInput);
        
        System.out.println(previousStore);
    }

    /// Load program into memory, starting at 0
    public void loadProgram(String filename, int startPosition) {
        System.out.println("Loading program " + filename + " at " + startPosition);
        try(DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            int lineCount = 0;

            ByteBuffer buffer = ByteBuffer.wrap(new byte[4]).order(ByteOrder.LITTLE_ENDIAN);
            
            while(inputStream.available() >= 4) {
                inputStream.read(buffer.array());
                buffer.rewind();
                memory[lineCount + (startPosition / wordLength)] = buffer.getInt();

                lineCount += 1;
            }
            
            // TODO: Allow current program instructions to be printed to terminal
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String toString() {
        return String.format("""
                Memory:
                    Output Location: %s
                    Previous Store: %s
                    Previous Load: %s""", memory[0], previousStore, previousLoad);
    }
}
