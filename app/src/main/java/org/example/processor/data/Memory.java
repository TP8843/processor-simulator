package org.example.processor.data;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Memory {
    /// Number of words in memory
    public final int size = 1024 * 1024;
    
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

        int value = (memory[pos / 4] >> ((pos % 4) * 8)) & 0xffff;

        if (unsigned) {
            previousLoad = String.format("Unsigned half word loaded from byte %s, array location %s, value %s",
                    pos, pos / 4, value);
            return value;
        } else {
            previousLoad = String.format("Signed half word loaded from byte %s, array location %s, value %s",
                    pos, pos / 4, (value << 16) >> 16);
            return (value << 16) >> 16;
        }
    }

    public int getByte(int pos, boolean unsigned) {
        int value = (memory[pos / 4] >> ((pos % 4) * 8)) & 0xff;

        if (unsigned) {
            previousLoad = String.format("Unsigned byte loaded from byte %s, array location %s, value %s",
                    pos, pos / 4, value);
            return value;
        } else {
            previousLoad = String.format("Signed byte loaded from byte %s, array location %s, value %s",
                    pos, pos / 4, (value << 16) >> 16);
            return (value << 16) >> 16;
        }
    }
    
    public void storeWord(int pos, int input) {
        if (pos % 4 != 0) throw new IllegalArgumentException("Memory access is not word aligned");
        
        memory[pos / 4] = input;

        previousStore = String.format("Word stored into byte %s, array location %s, value %s",
                pos, pos / 4, input);
    }

    public void storeHalfWord(int pos, int input) {
        if (pos % (4 / 2) != 0) throw new IllegalArgumentException("Memory access is not half word aligned");
        final int transformedInput = (input & 0xFFFF) << ((pos % 4) * 8);

        memory[pos / 4] = memory[pos / 4] | transformedInput;

        previousStore = String.format("Half word stored into byte %s, array location %s, value %s",
                pos, pos / 4, transformedInput);
    }

    public void storeByte(int pos, int input) {
        final int transformedInput = (input & 0xFF) << ((pos % 4) * 8);

        memory[pos / 4] = memory[pos / 4] | transformedInput;

        previousStore = String.format("Byte stored into byte %s, array location %s, value %s",
                pos, pos / 4, transformedInput);
    }

    /// Load program into memory, starting at 0
    public void loadProgram(String filename, int startPosition) {
        System.out.println("Loading program " + filename + " at " + startPosition);
        try(DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            int lineCount = 0;

            ByteBuffer buffer = ByteBuffer.wrap(new byte[4]).order(ByteOrder.LITTLE_ENDIAN);
            
            while(inputStream.available() > 0) {
                inputStream.read(buffer.array());
                buffer.rewind();
                memory[lineCount + (startPosition / 4)] = buffer.getInt();

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
                    Previous Store: %s
                    Previous Load: %s""", previousStore, previousLoad);
    }
}
