package org.example.processor;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class Memory {
    /// Number of words in memory
    public final int size = 6 * 1024;
    
    /// Number of bytes in word of memory
    public final int wordLength = 4;
    
    private int[] memory = new int[size /wordLength];
    
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
        
        System.out.println("Stored word " + input + " in memory at " + pos);
    }

    public void storeHalfWord(int pos, int input) {
        if (pos % (wordLength / 2) != 0) throw new IllegalArgumentException("Memory access is not half word aligned");

        memory[pos / wordLength] = memory[pos / wordLength] | ((input & 0xFF) << (((pos % wordLength) / 2) * 16));
        
        System.out.println("Stored half word " + input + " in memory at " + pos);
    }

    public void storeByte(int pos, int input) {
        memory[pos / wordLength] = memory[pos / wordLength] | ((input & 0xF) << ((pos % wordLength) * 8));
        
        System.out.println("Stored byte " + input + " in memory at " + pos);
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

                if (lineCount * 4 == 120){
                    System.out.println(String.format("Loaded instruction %32s into %s",
                            String.format("%32s", Integer.toBinaryString(memory[lineCount + startPosition])).replace(' ', '0'),
                            lineCount * 4 + startPosition));

                    System.out.println("After store " + Integer.toBinaryString(getWord(120)).replace(' ', '0'));
                }

                lineCount += 1;
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
