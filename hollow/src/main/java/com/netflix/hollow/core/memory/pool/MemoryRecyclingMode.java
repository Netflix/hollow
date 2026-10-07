package com.netflix.hollow.core.memory.pool;

/** Policy for selecting a consumer's array recycler, independently of experimental read features. */
public enum MemoryRecyclingMode {
    /** Select recycling according to the garbage collector, preserving the existing default. */
    AUTO,
    /** Reuse retired array segments, regardless of the garbage collector. */
    ENABLED,
    /** Allocate new segments rather than reusing retired arrays, regardless of the garbage collector. */
    DISABLED;

    public ArraySegmentRecycler createRecycler() {
        switch(this) {
            case AUTO:
                return new GarbageCollectorAwareRecycler();
            case ENABLED:
                return new RecyclingRecycler();
            case DISABLED:
                return new WastefulRecycler(ArraySegmentRecycler.DEFAULT_LOG2_BYTE_ARRAY_SIZE,
                        ArraySegmentRecycler.DEFAULT_LOG2_LONG_ARRAY_SIZE);
            default:
                throw new AssertionError(this);
        }
    }
}
