package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

/**
 * Correctness tests for the snapshot-backed map entry iterator returned by
 * {@link HollowMapTypeReadState#ordinalIterator(int)}, including a concurrency stress test that iterates maps
 * while another thread reshards the type state in place. Every entry produced by {@code generateMapContents}
 * satisfies {@code value == key + 1}; a torn read (mixing recycled memory across a shard swap) would surface an
 * entry violating that invariant.
 */
public class HollowMapSnapshotEntryOrdinalIteratorTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {

    /** Iterate the map's entries, asserting the value == key + 1 invariant; returns the entry count. */
    private int drainAndAssert(HollowMapTypeReadState readState, int ordinal) {
        HollowMapEntryOrdinalIterator iter = readState.ordinalIterator(ordinal);
        int count = 0;
        while (iter.next()) {
            int key = iter.getKey();
            int value = iter.getValue();
            if (value != key + 1)
                fail("Torn/incorrect entry at map ordinal " + ordinal + ": key=" + key + " value=" + value
                        + " (expected value=" + (key + 1) + ")");
            count++;
        }
        return count;
    }

    @Test
    public void iteratesMapEntries() throws IOException {
        int numRecords = 200;
        int[][][] mapContents = generateMapContents(numRecords);
        HollowMapTypeReadState readState = populateTypeStateWith(mapContents);

        boolean sawEntries = false;
        for (int ordinal = 0; ordinal <= readState.maxOrdinal(); ordinal++) {
            sawEntries |= drainAndAssert(readState, ordinal) > 0;
        }
        assertTrue(sawEntries);
    }

    @Test(timeout = 60_000)
    public void iterateConcurrentlyWhileResharding() throws Exception {
        int numRecords = 500;
        int[][][] mapContents = generateMapContents(numRecords);
        final HollowMapTypeReadState readState = populateTypeStateWith(mapContents);
        final HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(readState);

        final int numReaders = 6;
        final AtomicBoolean writerDone = new AtomicBoolean(false);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numReaders + 1);

        List<Thread> threads = new ArrayList<>();

        for (int r = 0; r < numReaders; r++) {
            final long seed = 0x9E3779B97F4A7C15L * (r + 1);
            Thread reader = new Thread(() -> {
                try {
                    startLatch.await();
                    long x = seed;
                    while (!writerDone.get() && failure.get() == null) {
                        x ^= x << 13; x ^= x >>> 7; x ^= x << 17;
                        int ordinal = (int) ((x >>> 1) % (readState.maxOrdinal() + 1));
                        drainAndAssert(readState, ordinal);
                    }
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                } finally {
                    doneLatch.countDown();
                }
            }, "map-reader-" + r);
            threads.add(reader);
        }

        Thread writer = new Thread(() -> {
            try {
                startLatch.await();
                for (int cycle = 0; cycle < 200 && failure.get() == null; cycle++) {
                    int cur = readState.numShards();
                    int target = cur < 8 ? cur * 2 : 1;
                    reshardingStrategy.reshard(readState, cur, target);
                }
            } catch (Throwable t) {
                failure.compareAndSet(null, t);
            } finally {
                writerDone.set(true);
                doneLatch.countDown();
            }
        }, "map-resharder");
        threads.add(writer);

        for (Thread t : threads) t.start();
        startLatch.countDown();
        assertTrue("threads did not finish in time", doneLatch.await(55, TimeUnit.SECONDS));
        for (Thread t : threads) t.join(TimeUnit.SECONDS.toMillis(5));

        if (failure.get() != null)
            throw new AssertionError("Concurrent iteration observed a failure", failure.get());

        assertDataUnchanged(readState, mapContents);
    }
}
