package org.example.processor;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private final Map<Integer, Integer> store;

    public Memory() {
        store = new HashMap<>();
    }

    public int getValue(int position) {
        if (!store.containsKey(position)) return 0;

        return store.get(position);
    }

    public void storeValue (int position, int value) {
        if (position < 0) return;

        store.put(position, value);

    }
    
    public String toString() {
        return store.toString();
    }
}
