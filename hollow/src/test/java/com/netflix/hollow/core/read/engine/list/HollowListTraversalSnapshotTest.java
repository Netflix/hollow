package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.api.perfapi.HollowListTypePerfAPI;
import com.netflix.hollow.api.perfapi.HollowPerfBackedList;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.Test;

public class HollowListTraversalSnapshotTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    @Test
    public void defaultJavaTraversalDoesNotSelectOrdinalIterators() throws Exception {
        HollowListTypeReadState type = spy(populateTypeStateWith(new int[][] {{0, 1, 2}}));
        for (List<? extends Number> list : lists(type)) {
            assertEquals(0, list.iterator().next().intValue());
            assertEquals(1, list.listIterator(1).next().intValue());
            assertEquals(Arrays.asList(0, 1, 2), list.stream().map(Number::intValue).collect(Collectors.toList()));
        }
        verify(type, never()).ordinalIterator(0);
    }

    @Test
    public void emptyShardCursorsUseJdkAdapters() throws Exception {
        HollowListTypeReadState type = populateTypeStateWith(new int[][] {{}});
        readStateEngine.setShardCursorIterators(true);
        for (List<? extends Number> list : lists(type)) {
            assertSame(Collections.emptyIterator(), list.iterator());
            assertSame(Collections.emptyListIterator(), list.listIterator());
            assertSame(Spliterators.emptySpliterator(), list.spliterator());
        }
    }

    @Test
    public void listIteratorRetainsImmutableSnapshotInBothDirections() throws Exception {
        HollowListTypeReadState type = immutableState();
        List<ListIterator<? extends Number>> iterators = new ArrayList<>();
        for (List<? extends Number> list : lists(type))
            iterators.add(list.listIterator(1));
        refresh();
        for (ListIterator<? extends Number> iterator : iterators) {
            assertEquals(0, iterator.previous().intValue());
            assertEquals(0, iterator.next().intValue());
            assertEquals(1, iterator.next().intValue());
            assertEquals(2, iterator.next().intValue());
            assertEquals(2, iterator.previous().intValue());
        }
    }

    @Test
    public void splitsRetainTheOriginalImmutableSnapshot() throws Exception {
        HollowListTypeReadState type = immutableState();
        List<Spliterator<? extends Number>> suffixes = new ArrayList<>();
        List<Spliterator<? extends Number>> prefixes = new ArrayList<>();
        for (List<? extends Number> list : lists(type)) {
            Spliterator<? extends Number> suffix = list.spliterator();
            suffixes.add(suffix);
            prefixes.add(suffix.trySplit());
        }
        refresh();
        for (int i = 0; i < suffixes.size(); i++) {
            List<Integer> values = new ArrayList<>();
            prefixes.get(i).forEachRemaining(value -> values.add(value.intValue()));
            Spliterator<? extends Number> tail = suffixes.get(i);
            tail.trySplit().forEachRemaining(value -> values.add(value.intValue()));
            tail.forEachRemaining(value -> values.add(value.intValue()));
            assertEquals(Arrays.asList(0, 1, 2), values);
        }
    }

    @Test
    public void parallelTraversalSurvivesRecycledResharding() throws Exception {
        HollowListTypeReadState type = populateTypeStateWith(generateListContents(128));
        readStateEngine.setShardCursorIterators(true);
        for (List<? extends Number> list : lists(type, 127)) {
            Spliterator<? extends Number> suffix = list.spliterator();
            Spliterator<? extends Number> prefix = suffix.trySplit();
            HollowTypeReshardingStrategy.getInstance(type).reshard(type, type.numShards(), 2);
            List<Integer> values = new ArrayList<>();
            prefix.forEachRemaining(value -> values.add(value.intValue()));
            suffix.forEachRemaining(value -> values.add(value.intValue()));
            assertEquals(128, values.size());
            for (int i = 0; i < values.size(); i++)
                assertEquals(i, values.get(i).intValue());
            assertEquals(values, list.parallelStream().map(Number::intValue).collect(Collectors.toList()));
            HollowTypeReshardingStrategy.getInstance(type).reshard(type, type.numShards(), 1);
        }
    }

    @Test(timeout = 60_000)
    public void parallelSplitsValidateIndependentlyDuringResharding() throws Exception {
        HollowListTypeReadState type = populateTypeStateWith(generateListContents(128));
        readStateEngine.setShardCursorIterators(true);
        List<Integer> expected = IntStream.range(0, 128).boxed().collect(Collectors.toList());
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread writer = new Thread(() -> {
            try {
                start.await();
                for (int i = 0; i < 100; i++) {
                    int count = type.numShards();
                    HollowTypeReshardingStrategy.getInstance(type).reshard(type, count, count < 4 ? count * 2 : 1);
                }
            } catch (Throwable t) {
                failure.set(t);
            }
        }, "list-split-resharder");
        writer.start();
        start.countDown();
        try {
            for (int i = 0; i < 20; i++) {
                for (List<? extends Number> list : lists(type, 127))
                    assertEquals(expected, list.parallelStream().map(Number::intValue).collect(Collectors.toList()));
            }
        } finally {
            writer.join(30_000);
        }
        assertTrue("resharder did not finish", !writer.isAlive());
        if (failure.get() != null)
            throw new AssertionError(failure.get());
    }

    private HollowListTypeReadState immutableState() throws Exception {
        populateWriteStateEngine(3);
        populateWriteStateEngineWithListRecords(new int[][] {{0, 1, 2}});
        readStateEngine = new HollowReadStateEngine(WastefulRecycler.DEFAULT_INSTANCE);
        readStateEngine.setShardCursorIterators(true);
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);
        return (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
    }

    private void refresh() throws Exception {
        populateWriteStateEngine(writeStateEngine, schema, 3);
        populateWriteStateEngineWithListRecords(new int[][] {{2}});
        roundTripDelta();
        // Retire the ghost and reuse its ordinal so live get() can no longer reproduce the snapshot.
        populateWriteStateEngine(writeStateEngine, schema, 3);
        populateWriteStateEngineWithListRecords(new int[][] {{1}});
        roundTripDelta();
    }

    private List<List<? extends Number>> lists(HollowListTypeReadState type) {
        return lists(type, 0);
    }

    private List<List<? extends Number>> lists(HollowListTypeReadState type, int ordinal) {
        HollowList<Integer> generic = new HollowList<Integer>(new HollowListLookupDelegate<>(type), ordinal) {
            @Override
            public Integer instantiateElement(int elementOrdinal) {
                return elementOrdinal;
            }

            @Override
            public boolean equalsElement(int elementOrdinal, Object testObject) {
                return Integer.valueOf(elementOrdinal).equals(testObject);
            }
        };
        HollowListTypePerfAPI api = mock(HollowListTypePerfAPI.class);
        when(api.typeAccess()).thenReturn(type);
        return Arrays.asList(generic, new HollowPerfBackedList<>(api, ordinal, ref -> ref));
    }
}
