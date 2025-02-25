package org.example.processor.buffers;

import java.util.Optional;

public class SingleValueBuffer<T> implements Buffer<T> {
    /// Value stored inside the buffer
    private T value;
    
    /// True if buffer is stalled
    private boolean stalled;

    @Override
    public Optional<T> get() {
        if (value != null && !stalled) {
            T value = this.value;
            this.value = null;
            return Optional.of(value);
        } else 
            return Optional.empty();
    }

    @Override
    public boolean put(T value) {
        if (this.value != null) {
            return false;
        } else {
            this.value = value;
            return true;
        }
    }

    @Override
    public boolean hasSpace() {
        return value == null;
    }

    @Override
    public void stall() {
        stalled = true;
    }

    @Override
    public void release() {
        stalled = false;
    }
}
