package com.netflix.hollow.core.memory.pool;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryManagerMXBean;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link ArraySegmentRecycler} that chooses the appropriate recycler based on the garbage collector in use.
 * <p>
 * Specifically, when a low-pause collector is in use where promotion/evacuation pauses are no longer a concern,
 * delegate to {@link WastefulRecycler}. Otherwise the default {@link RecyclingRecycler} is used.
 */
public class GarbageCollectorAwareRecycler implements ArraySegmentRecycler {
    private static final List<String> LOW_PAUSE_COLLECTORS = Arrays.asList("GPGC", "Shenandoah", "ZGC");

    private final ArraySegmentRecycler delegate;

    public GarbageCollectorAwareRecycler() {
        this(DEFAULT_LOG2_BYTE_ARRAY_SIZE, DEFAULT_LOG2_LONG_ARRAY_SIZE);
    }

    public GarbageCollectorAwareRecycler(int log2OfByteSegmentSize, int log2OfLongSegmentSize) {
        boolean isLowPause = ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .map(MemoryManagerMXBean::getName)
                .map(name -> name.split(" ")[0]) // In the form '<GC> <Phase>', for instance 'PS Scavenge' or 'ZGC Major Pauses'
                .distinct()
                .findFirst()
                .map(LOW_PAUSE_COLLECTORS::contains)
                .orElse(false);
        delegate = isLowPause ? new WastefulRecycler(log2OfByteSegmentSize, log2OfLongSegmentSize)
                : new RecyclingRecycler(log2OfByteSegmentSize, log2OfLongSegmentSize);
    }

    @Override
    public int getLog2OfByteSegmentSize() {
        return delegate.getLog2OfByteSegmentSize();
    }

    @Override
    public int getLog2OfLongSegmentSize() {
        return delegate.getLog2OfLongSegmentSize();
    }

    @Override
    public long[] getLongArray() {
        return delegate.getLongArray();
    }

    @Override
    public void recycleLongArray(long[] arr) {
        delegate.recycleLongArray(arr);
    }

    @Override
    public byte[] getByteArray() {
        return delegate.getByteArray();
    }

    @Override
    public void recycleByteArray(byte[] arr) {
        delegate.recycleByteArray(arr);
    }

    @Override
    public void swap() {
        delegate.swap();
    }
}
