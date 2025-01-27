package org.example.processor;

public class Memory {
    private int size = 512;

    private final int[] store;

    public Memory(int size) {
        this.size = size;
        store = new int[size];
    }

    public Memory() {
        store = new int[size];
    }

    public int getValue(int position) {
        if (position < 0 || position >= size) return 0;

        return store[position];
    }

    public void storeValue (int position, int value) {
        if (position < 0 || position >= size) return;

        store[position] = value;
    }
}
