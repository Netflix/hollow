package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Compares list iteration cost of the per-element read path (the historical {@code HollowListOrdinalIterator}
 * pattern: {@code size()} + {@code getElementOrdinal(ordinal, i)} per element, i.e. 2N+1 {@code Unsafe.loadFence()}s)
 * against the single-snapshot path ({@code ordinalIterator()} backed by
 * {@code HollowListTypeReadState.readElementOrdinals}, i.e. 2 fences for the whole list).
 *
 * The gap should widen with list size, since the per-element path pays two fences per element while the snapshot
 * path pays two fences total.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 6, time = 1)
@Fork(1)
public class HollowListTypeReadStateIterationBenchmark {

    static class ListHolder {
        List<Integer> vals;
        ListHolder(List<Integer> vals) { this.vals = vals; }
    }

    @Param({ "1", "4", "16", "64", "256" })
    int listSize;

    @Param({ "2000" })
    int numLists;

    HollowListTypeReadState listTypeState;
    int[] ordinals;

    @Setup
    public void setUp() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(ListHolder.class);

        for (int i = 0; i < numLists; i++) {
            List<Integer> vals = new ArrayList<>(listSize);
            for (int j = 0; j < listSize; j++)
                vals.add(i + j); // distinct lists so they aren't deduped to one ordinal
            objectMapper.add(new ListHolder(vals));
        }

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        listTypeState = null;
        for (HollowTypeReadState ts : readStateEngine.getTypeStates()) {
            if (ts instanceof HollowListTypeReadState) {
                listTypeState = (HollowListTypeReadState) ts;
                break;
            }
        }
        if (listTypeState == null)
            throw new IllegalStateException("no list type state found");

        int max = listTypeState.maxOrdinal();
        List<Integer> populated = new ArrayList<>();
        for (int o = 0; o <= max; o++)
            populated.add(o);
        ordinals = new int[populated.size()];
        for (int i = 0; i < ordinals.length; i++)
            ordinals[i] = populated.get(i);
    }

    /** New path: one validated snapshot per list (2 fences), served from the materialized iterator. */
    @Benchmark
    public void iterateSnapshot(Blackhole bh) {
        for (int ordinal : ordinals) {
            HollowOrdinalIterator iter = listTypeState.ordinalIterator(ordinal);
            int o = iter.next();
            while (o != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                bh.consume(o);
                o = iter.next();
            }
        }
    }

    /** Old path: size() then getElementOrdinal(ordinal, i) per element (2N+1 fences per list). */
    @Benchmark
    public void iteratePerElement(Blackhole bh) {
        for (int ordinal : ordinals) {
            int size = listTypeState.size(ordinal);
            for (int i = 0; i < size; i++) {
                bh.consume(listTypeState.getElementOrdinal(ordinal, i));
            }
        }
    }
}
