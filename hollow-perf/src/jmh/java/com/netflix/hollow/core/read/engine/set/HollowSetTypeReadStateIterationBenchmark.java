package com.netflix.hollow.core.read.engine.set;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
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
 * Compares set iteration cost of the per-bucket read path (the historical {@code HollowSetOrdinalIterator}
 * pattern: {@code size()} + {@code relativeBucketValue(ordinal, bucket)} over every hash bucket, i.e. two fences
 * per bucket) against the single-snapshot iterator ({@code ordinalIterator()}, one fence per surfaced element).
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 6, time = 1)
@Fork(1)
public class HollowSetTypeReadStateIterationBenchmark {

    static class SetHolder {
        Set<Integer> vals;
        SetHolder(Set<Integer> vals) { this.vals = vals; }
    }

    @Param({ "1", "4", "16", "64", "256" })
    int setSize;

    @Param({ "2000" })
    int numSets;

    HollowSetTypeReadState setTypeState;
    int[] ordinals;

    @Setup
    public void setUp() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(SetHolder.class);

        for (int i = 0; i < numSets; i++) {
            Set<Integer> vals = new LinkedHashSet<>();
            for (int j = 0; j < setSize; j++)
                vals.add(i + j); // distinct sets so they aren't deduped to one ordinal
            objectMapper.add(new SetHolder(vals));
        }

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        setTypeState = null;
        for (HollowTypeReadState ts : readStateEngine.getTypeStates()) {
            if (ts instanceof HollowSetTypeReadState) {
                setTypeState = (HollowSetTypeReadState) ts;
                break;
            }
        }
        if (setTypeState == null)
            throw new IllegalStateException("no set type state found");

        int max = setTypeState.maxOrdinal();
        ordinals = new int[max + 1];
        for (int o = 0; o <= max; o++)
            ordinals[o] = o;
    }

    /** New path: one validated snapshot per set, one fence per surfaced element. */
    @Benchmark
    public void iterateSnapshot(Blackhole bh) {
        for (int ordinal : ordinals) {
            HollowOrdinalIterator iter = setTypeState.ordinalIterator(ordinal);
            int o = iter.next();
            while (o != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                bh.consume(o);
                o = iter.next();
            }
        }
    }

    /** Old path: size() then relativeBucketValue(ordinal, bucket) over every hash bucket (2 fences per bucket). */
    @Benchmark
    public void iteratePerBucket(Blackhole bh) {
        for (int ordinal : ordinals) {
            int numBuckets = HashCodes.hashTableSize(setTypeState.size(ordinal));
            for (int b = 0; b < numBuckets; b++) {
                int v = setTypeState.relativeBucketValue(ordinal, b);
                if (v != ORDINAL_NONE)
                    bh.consume(v);
            }
        }
    }
}
