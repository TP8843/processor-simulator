package org.example.processor.buffers;

import java.util.Optional;

public interface Buffer<T> extends Flushable {
    /// Gets the next available T, removing it from the buffer, or else returns none
    Optional<T> pop();
    
    /// Gets the next available T, but does not remove it from the buffer, or else returns none
    Optional<T> peek();
    
    /// Adds a T to the buffer and returns true if successful, else returns false
    boolean put(T value);
    
    /// Returns true if there is space on the buffer, else returns false
    boolean hasSpace();
    
    /// Returns true if there is a poppable value on the buffer, else returns false
    boolean hasValue();
    
    /// Stalls the buffer (stops it from being emptied)
    void stall();
    
    /// Releases the stall on the buffer (allows it to be emptied)
    void release();
    
    /// Flushes all values from the buffer
    void flush();
    
    /// Outputs the current value of the buffer
    String toString();
}
