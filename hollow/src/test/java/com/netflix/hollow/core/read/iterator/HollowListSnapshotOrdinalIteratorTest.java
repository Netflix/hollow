package com.netflix.hollow.core.read.iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.read.engine.list.AbstractHollowListTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.util.IntList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;

/**
 * Correctness tests for the snapshot-backed list ordinal iterator returned by
 * {@link HollowListTypeReadState#ordinalIterator(int)}, including a concurrency stress test that iterates lists
 * while another thread reshards the type state in place. Resharding atomically swaps {@code shardsVolatile} and
 * then recycles the old {@code dataElements}' backing arrays, which is precisely the window the iterator's
 * validated-snapshot read protocol must tolerate. Every list produced by {@code generateListContents} is the
 * contiguous run {@code [0, 1, ..., k]}, so any correct read satisfies {@code v[j] == j}; a torn read (mixing
 * recycled memory across a shard swap) would violate that invariant.
 */
public class HollowListSnapshotOrdinalIteratorTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    private IntList drain(HollowListTypeReadState readState, int ordinal) {
        HollowOrdinalIterator iter = readState.ordinalIterator(ordinal);
        IntList out = new IntList();
        int o = iter.next();
        while (o != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            out.add(o);
            o = iter.next();
        }
        return out;
    }

    /** A valid list of size n is exactly [0, 1, ..., n-1]. */
    private void assertContiguousFromZero(IntList actual, int ordinal) {
        for (int j = 0; j < actual.size(); j++) {
            if (actual.get(j) != j) {
                fail("Torn/incorrect read at ordinal " + ordinal + ", index " + j
                        + ": expected " + j + " but got " + actual.get(j));
            }
        }
    }

    @Test
    public void iteratesEmptyAndSingleAndMultiElementLists() throws IOException {
        // sizes 1..50 (generateListContents produces list i of size i+1)
        int numRecords = 50;
        int[][] listContents = generateListContents(numRecords);
        HollowListTypeReadState readState = populateTypeStateWith(listContents);

        boolean sawSizeOne = false;
        boolean sawLarge = false;
        for (int ordinal = 0; ordinal <= readState.maxOrdinal(); ordinal++) {
            IntList actual = drain(readState, ordinal);
            assertContiguousFromZero(actual, ordinal);
            sawSizeOne |= actual.size() == 1;
            sawLarge |= actual.size() == numRecords;
        }
        assertTrue(sawSizeOne);
        assertTrue(sawLarge);
    }

    @Test
    public void iteratorExhaustsCleanlyAndRepeatsNoMoreOrdinals() throws IOException {
        HollowListTypeReadState readState = populateTypeStateWith(generateListContents(10));
        HollowOrdinalIterator iter = readState.ordinalIterator(0);
        int count = 0;
        while (iter.next() != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            count++;
        }
        assertTrue(count > 0);
        // Past the end, next() must keep returning NO_MORE_ORDINALS.
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
    }

    @Test
    public void iteratorMatchesPerElementReads() throws IOException {
        HollowListTypeReadState readState = populateTypeStateWith(generateListContents(100));
        for (int ordinal = 0; ordinal <= readState.maxOrdinal(); ordinal++) {
            int size = readState.size(ordinal);
            IntList iterated = drain(readState, ordinal);
            assertEquals("size mismatch at ordinal " + ordinal, size, iterated.size());
            for (int i = 0; i < size; i++) {
                assertEquals("element mismatch at ordinal " + ordinal + " index " + i,
                        readState.getElementOrdinal(ordinal, i), iterated.get(i));
            }
        }
    }

    @Test(timeout = 60_000)
    public void iterateConcurrentlyWhileResharding() throws Exception {
        int numRecords = 1000;
        int[][] listContents = generateListContents(numRecords);
        final HollowListTypeReadState readState = populateTypeStateWith(listContents);
        final HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(readState);

        final int numReaders = 6;
        final AtomicBoolean writerDone = new AtomicBoolean(false);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(numReaders + 1);

        List<Thread> threads = new ArrayList<>();

        // Reader threads: continuously iterate random lists and validate the contiguous invariant.
        for (int r = 0; r < numReaders; r++) {
            final long seed = 0x9E3779B97F4A7C15L * (r + 1); // deterministic per-reader, no shared RNG
            Thread reader = new Thread(() -> {
                try {
                    startLatch.await();
                    long x = seed;
                    while (!writerDone.get() && failure.get() == null) {
                        // xorshift for a cheap, allocation-free, thread-local pseudo-random ordinal
                        x ^= x << 13; x ^= x >>> 7; x ^= x << 17;
                        int ordinal = (int) ((x >>> 1) % (readState.maxOrdinal() + 1));
                        IntList actual = drain(readState, ordinal);
                        assertContiguousFromZero(actual, ordinal);
                    }
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                } finally {
                    doneLatch.countDown();
                }
            }, "list-reader-" + r);
            threads.add(reader);
        }

        // Writer thread: repeatedly split and join shards, swapping shardsVolatile and recycling old memory.
        Thread writer = new Thread(() -> {
            try {
                startLatch.await();
                for (int cycle = 0; cycle < 300 && failure.get() == null; cycle++) {
                    int cur = readState.numShards();
                    int target = cur < 8 ? cur * 2 : 1; // ...->2->4->8->1->2->...
                    reshardingStrategy.reshard(readState, cur, target);
                }
            } catch (Throwable t) {
                failure.compareAndSet(null, t);
            } finally {
                writerDone.set(true);
                doneLatch.countDown();
            }
        }, "list-resharder");
        threads.add(writer);

        for (Thread t : threads) t.start();
        startLatch.countDown();
        assertTrue("threads did not finish in time", doneLatch.await(55, TimeUnit.SECONDS));
        for (Thread t : threads) t.join(TimeUnit.SECONDS.toMillis(5));

        if (failure.get() != null) {
            throw new AssertionError("Concurrent iteration observed a failure", failure.get());
        }

        // Data must be fully intact after all the resharding churn.
        assertDataUnchanged(readState, listContents);
    }
}
