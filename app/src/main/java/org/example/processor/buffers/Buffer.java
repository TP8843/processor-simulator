package org.example.processor.buffers;

import java.util.Optional;

public interface Buffer<T> {
    /// Gets the next available T if it is available, or else returns none
    Optional<T> get();
    
    /// Adds a T to the buffer and returns true if successful, else returns false
    boolean put(T value);
    
    /// Returns true if there is space on the buffer, else returns false
    boolean hasSpace();
    
    /// Stalls the buffer (stops it from being emptied)
    void stall();
    
    /// Releases the stall on the buffer (allows it to be emptied)
    void release();
    
    /// Outputs the current value of the buffer
    String toString();
}
