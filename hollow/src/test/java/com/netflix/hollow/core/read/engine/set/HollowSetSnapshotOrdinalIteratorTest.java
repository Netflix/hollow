package com.netflix.hollow.core.read.engine.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.fail;

import com.netflix.hollow.api.sampling.EnabledSamplingDirector;
import com.netflix.hollow.api.sampling.SampleResult;
import com.netflix.hollow.core.read.iterator.EmptyOrdinalIterator;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowSetOrdinalIterator;
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
        readStateEngine.setShardCursorIterators(true);
        HollowOrdinalIterator iterator = readState.ordinalIterator(0);
        assertTrue(iterator instanceof HollowSetSnapshotOrdinalIterator);
        assertTrue(iterator instanceof HollowSetOrdinalIterator);

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

    @Test
    public void capturesSizeAndBoundsTogetherAndPreservesSampling() throws IOException {
        HollowSetTypeReadState readState = spy(populateTypeStateWith(new int[][] { {}, {0} }));
        readStateEngine.setShardCursorIterators(true);
        readState.setSamplingDirector(new EnabledSamplingDirector());
        HollowOrdinalIterator empty = readState.ordinalIterator(0);
        assertSame(EmptyOrdinalIterator.INSTANCE, empty);
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, empty.next());
        HollowOrdinalIterator nonempty = readState.ordinalIterator(1);
        verify(readState, times(1)).readWasUnsafe(any(), eq(0), any());
        verify(readState, times(1)).readWasUnsafe(any(), eq(1), any());
        verify(readState, never()).size(0);
        verify(readState, never()).size(1);
        assertEquals(0, nonempty.next());
        for (SampleResult result : readState.getSampler().getSampleResults()) {
            if (result.getIdentifier().endsWith(".size()"))
                assertEquals(3, result.getNumSamples());
            if (result.getIdentifier().endsWith(".iterator()"))
                assertEquals(2, result.getNumSamples());
        }
    }

    @Test(timeout = 60_000)
    public void iterateConcurrentlyWhileResharding() throws Exception {
        int numRecords = 500;
        int[][] setContents = generateSetContents(numRecords);
        final HollowSetTypeReadState readState = populateTypeStateWith(setContents);
        readStateEngine.setShardCursorIterators(true);
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
