package com.netflix.hollow.core.read.engine.set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
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
 * Correctness tests for the snapshot-backed set ordinal iterator returned by
 * {@link HollowSetTypeReadState#ordinalIterator(int)}, including a concurrency stress test that iterates sets
 * while another thread reshards the type state in place. Every set produced by {@code generateSetContents} is the
 * element set {@code {0, 1, ..., k}}, so a correct read of any set yields exactly {@code {0 .. count-1}} with no
 * gaps or duplicates; a torn read (mixing recycled memory across a shard swap) would surface an out-of-range or
 * duplicate element.
 */
public class HollowSetSnapshotOrdinalIteratorTest extends AbstractHollowSetTypeDataElementsSplitJoinTest {

    private IntList drain(HollowSetTypeReadState readState, int ordinal) {
        HollowOrdinalIterator iter = readState.ordinalIterator(ordinal);
        IntList out = new IntList();
        int o = iter.next();
        while (o != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            out.add(o);
            o = iter.next();
        }
        return out;
    }

    /** A valid set of count elements is exactly {0, 1, ..., count-1} (order is hash-dependent). */
    private void assertContiguousSet(IntList actual, int ordinal) {
        int n = actual.size();
        boolean[] seen = new boolean[n];
        for (int i = 0; i < n; i++) {
            int v = actual.get(i);
            if (v < 0 || v >= n)
                fail("Torn/incorrect read at set ordinal " + ordinal + ": element " + v + " out of range [0," + n + ")");
            if (seen[v])
                fail("Torn/incorrect read at set ordinal " + ordinal + ": duplicate element " + v);
            seen[v] = true;
        }
    }

    @Test
    public void iteratesEmptyAndSingleAndMultiElementSets() throws IOException {
        int numRecords = 50;
        int[][] setContents = generateSetContents(numRecords);
        HollowSetTypeReadState readState = populateTypeStateWith(setContents);

        boolean sawSizeOne = false;
        boolean sawLarge = false;
        for (int ordinal = 0; ordinal <= readState.maxOrdinal(); ordinal++) {
            IntList actual = drain(readState, ordinal);
            assertContiguousSet(actual, ordinal);
            sawSizeOne |= actual.size() == 1;
            sawLarge |= actual.size() == numRecords;
        }
        assertTrue(sawSizeOne);
        assertTrue(sawLarge);
    }

    @Test(timeout = 60_000)
    public void iterateConcurrentlyWhileResharding() throws Exception {
        int numRecords = 500;
        int[][] setContents = generateSetContents(numRecords);
        final HollowSetTypeReadState readState = populateTypeStateWith(setContents);
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
                        assertContiguousSet(drain(readState, ordinal), ordinal);
                    }
                } catch (Throwable t) {
                    failure.compareAndSet(null, t);
                } finally {
                    doneLatch.countDown();
                }
            }, "set-reader-" + r);
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
        }, "set-resharder");
        threads.add(writer);

        for (Thread t : threads) t.start();
        startLatch.countDown();
        assertTrue("threads did not finish in time", doneLatch.await(55, TimeUnit.SECONDS));
        for (Thread t : threads) t.join(TimeUnit.SECONDS.toMillis(5));

        if (failure.get() != null)
            throw new AssertionError("Concurrent iteration observed a failure", failure.get());

        assertDataUnchanged(readState, setContents);
    }
}
