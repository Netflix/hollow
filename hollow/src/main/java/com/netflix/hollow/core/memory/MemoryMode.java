package com.netflix.hollow.core.memory;

public enum MemoryMode {

    ON_HEAP,                // eager load into main memory, on JVM heap
    SHARED_MEMORY_LAZY,     // map to virtual memory and lazy load into main memory, off heap
    SHARED_MEMORY_EAGER;    // map to virtual memory and eager load into main memory, off heap

    /*
     * Returns whether a memory mode is supported by Hollow consumer
     */
    public boolean consumerSupported() {
        return this.equals(ON_HEAP) || this.equals(SHARED_MEMORY_LAZY);
    }

    /*
     * Returns whether a memory mode supports type filtering
     */
    public boolean supportsFiltering() {
        return this.equals(ON_HEAP);
    }


}
