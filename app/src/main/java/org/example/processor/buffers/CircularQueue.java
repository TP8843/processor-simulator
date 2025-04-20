package org.example.processor.buffers;

import org.example.processor.instructions.Instruction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class CircularQueue<T> implements Flushable, Iterable<T> {
    /// Size of the circular queue
    public final int size;

    /// Head of the queue
    private int head = 0;

    /// Tail of the queue
    private int tail = 0;

    /// Backing array for queue
    private final Object[] data;

    /// Default constructor. Creates a queue of max length 16
    public CircularQueue(){
        this(16);
    }

    /// Creates a queue of size
    public CircularQueue(int size){
        this.size = size;
        data = new Object[size];
    }

    /// Whether the queue is currently full
    public boolean isFull(){
        return (tail - head == size - 1) || (tail - head == -1);
    }

    /// Whether the queue has any items
    public boolean isEmpty(){
        return tail == head;
    }

    /// Peek at item at top of queue
    public Optional<T> peek(){
        if(isEmpty()) return Optional.empty();

        @SuppressWarnings("unchecked")
        Optional<T> item = Optional.of((T) data[head]);
        return item;
    }

    /// Pops element from the queue
    public Optional<T> dequeue(){
        if(isEmpty()) return Optional.empty();

        @SuppressWarnings("unchecked")
        Optional<T> item = Optional.of((T) data[head]);
        head = (head + 1) % size;
        return item;
    }

    public void enqueue(T item){
        if(isFull()) return;

        data[tail] = item;
        tail = (tail + 1) % size;
    }

    /// Flush all elements from the queue
    @Override
    public void flush(){
        head = 0;
        tail = 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Circular Queue (size = %s):\n", size));

        int i = head;
        int j = 0;

        while (i != tail) {
            builder.append(String.format("Item %d: %s\n", j, data[i]));
            i = (i + 1) % size;
            j += 1;
        }

        return builder.toString();
    }

    @Override
    public Iterator<T> iterator(){
        return new Iterator<T>() {
            int i = head;

            @Override
            public boolean hasNext() {
                return i <= tail;
            }

            @Override
            public T next() {
                i = i + 1;
                if(i >= size) i -= size;

                @SuppressWarnings("unchecked")
                T value = (T) data[i];
                return value;
            }
        };
    }

    public Iterator<T> reverseIterator() {
        return new Iterator<T>() {
            int i = tail;

            @Override
            public boolean hasNext() {
                return i != head;
            }

            @Override
            public T next() {
                i = Math.floorMod(i - 1, size);

                @SuppressWarnings("unchecked")
                T value = (T) data[i];
                return value;
            }
        };
    }
}
