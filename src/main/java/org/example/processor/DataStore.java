package org.example.processor;

public class DataStore {
    private int size = 32;

    private int[] store;

    public DataStore(int size) {
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
