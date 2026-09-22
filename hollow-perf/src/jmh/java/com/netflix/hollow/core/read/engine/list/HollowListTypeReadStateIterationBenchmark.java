package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
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
 * pattern: {@code size()} + {@code getElementOrdinal(ordinal, i)} per element, i.e. 2N+1
 * {@code Unsafe.loadFence()}s when arrays can be recycled)
 * against the single-snapshot path ({@code ordinalIterator()} backed by a shard cursor). With recycling, the
 * cursor validates once per element; without recycling, its captured shard is immutable and requires no trailing
 * validation.
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

    @Param({ "recycling", "wasteful" })
    String recycler;

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

        ArraySegmentRecycler memoryRecycler = recycler.equals("recycling")
                ? new RecyclingRecycler()
                : WastefulRecycler.DEFAULT_INSTANCE;
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(memoryRecycler);
        readStateEngine.setSnapshotCollectionIterators(true);
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

    /** New path: one shard snapshot per list, validated per element only when arrays can be recycled. */
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

    /** Old path: size() then getElementOrdinal(ordinal, i) per element. */
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
