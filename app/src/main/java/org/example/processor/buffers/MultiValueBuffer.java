package org.example.processor.buffers;

import org.example.processor.instructions.Instruction;

import java.util.Optional;

public class MultiValueBuffer<T> implements Buffer<T>, Flushable {
    /// Backing queue for buffer
    private final CircularQueue<T> queue;

    /// True if buffer is stalled
    private boolean stalled = false;

    public MultiValueBuffer(int size) {
        this.queue = new CircularQueue<>(size);
    }

    @Override
    public Optional<T> pop() {
        if (!queue.isEmpty() && !stalled) {
            return Optional.of(queue.dequeue().get());
        } else
            return Optional.empty();
    }

    @Override
    public Optional<T> peek() {
        if(!queue.isEmpty() && !stalled) {
            return Optional.of(queue.dequeue().get());
        }

        return Optional.empty();
    }

    @Override
    public boolean put(T value) {
        if (queue.isFull()) return false;

        queue.enqueue(value);
        return true;
    }

    @Override
    public boolean hasSpace() {
        return !queue.isFull();
    }

    @Override
    public boolean hasValue() {
        return !stalled && !queue.isEmpty();
    }

    @Override
    public void stall() {
        stalled = true;
    }

    @Override
    public void release() {
        stalled = false;
    }

    @Override
    public void flush() {
        queue.flush();
    }

    @Override
    public String toString() {
        return String.format("""
                Stalled: %s
                Items: %s""", stalled ? "True" : "False", queue);
    }
}
