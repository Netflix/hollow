package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
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
 * Compares map entry iteration cost of the per-bucket read path (the historical
 * {@code HollowMapEntryOrdinalIteratorImpl} pattern: {@code size()} + {@code relativeBucket(ordinal, bucket)}
 * over every hash bucket, i.e. two fences per bucket) against the single-snapshot entry iterator
 * ({@code ordinalIterator()}, one fence per surfaced entry).
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 6, time = 1)
@Fork(1)
public class HollowMapTypeReadStateIterationBenchmark {

    static class MapHolder {
        Map<Integer, Integer> vals;
        MapHolder(Map<Integer, Integer> vals) { this.vals = vals; }
    }

    @Param({ "1", "4", "16", "64", "256" })
    int mapSize;

    @Param({ "2000" })
    int numMaps;

    HollowMapTypeReadState mapTypeState;
    int[] ordinals;

    @Setup
    public void setUp() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(MapHolder.class);

        for (int i = 0; i < numMaps; i++) {
            Map<Integer, Integer> vals = new LinkedHashMap<>();
            for (int j = 0; j < mapSize; j++)
                vals.put(i + j, i + j + 1); // distinct maps so they aren't deduped to one ordinal
            objectMapper.add(new MapHolder(vals));
        }

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        mapTypeState = null;
        for (HollowTypeReadState ts : readStateEngine.getTypeStates()) {
            if (ts instanceof HollowMapTypeReadState) {
                mapTypeState = (HollowMapTypeReadState) ts;
                break;
            }
        }
        if (mapTypeState == null)
            throw new IllegalStateException("no map type state found");

        int max = mapTypeState.maxOrdinal();
        ordinals = new int[max + 1];
        for (int o = 0; o <= max; o++)
            ordinals[o] = o;
    }

    /** New path: one validated snapshot per map, one fence per surfaced entry. */
    @Benchmark
    public void iterateSnapshot(Blackhole bh) {
        for (int ordinal : ordinals) {
            HollowMapEntryOrdinalIterator iter = mapTypeState.ordinalIterator(ordinal);
            while (iter.next()) {
                bh.consume(iter.getKey());
                bh.consume(iter.getValue());
            }
        }
    }

    /** Old path: size() then relativeBucket(ordinal, bucket) over every hash bucket (2 fences per bucket). */
    @Benchmark
    public void iteratePerBucket(Blackhole bh) {
        for (int ordinal : ordinals) {
            int numBuckets = HashCodes.hashTableSize(mapTypeState.size(ordinal));
            for (int b = 0; b < numBuckets; b++) {
                long bucketVal = mapTypeState.relativeBucket(ordinal, b);
                if (bucketVal != -1L) {
                    bh.consume((int) (bucketVal >>> 32));
                    bh.consume((int) bucketVal);
                }
            }
        }
    }
}
