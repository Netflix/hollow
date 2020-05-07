package com.netflix.hollow.core.memory;

public class MemoryMode {
    public enum Mode {
        ON_HEAP,
        SHARED_MEMORY
    }

    public static Mode getMemoryMode() {
        return Mode.SHARED_MEMORY;
    }


}
