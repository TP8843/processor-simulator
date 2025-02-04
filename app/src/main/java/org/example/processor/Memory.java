package org.example.processor;

public class Memory {
    /// Number of words in memory
    public final int size = 2048;
    
    /// Number of bytes in word of memory
    public final int wordLength = 4;
    
    private int[] memory = new int[size];
    
    public int getWord(int pos) {
        if (pos % wordLength != 0) throw new IllegalArgumentException("Memory access is not word aligned");
        
        return memory[pos / 4];
    }
    
    public int getHalfWord(int pos) {
        if (pos % (wordLength / 2) != 0) throw new IllegalArgumentException("Memory access is not half word aligned");
        
        return (memory[pos / 4] >> ((pos % 4) * 8) & 0b1111111111111111);
    }

    public int getByte(int pos) {
        return (memory[pos / 4] >> ((pos % 4) * 8) & 0b11111111);
    }
    
    public void storeWord(int pos, int word) {
        if (pos % wordLength != 0) throw new IllegalArgumentException("Memory access is not word aligned");
        
        memory[pos / 4] = word;
    }
}
