package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.Random;
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
 * End-to-end benchmark of {@link HollowObjectTypeDataAccess#readLong} across a range of value
 * magnitudes. The field's bitsPerField is derived from the maximum encoded value, so maxBits
 * controls whether the read takes the single-read (getElementValue, <=56 bits) or two-word
 * (getLargeElementValue) path in HollowObjectTypeReadStateShard.readLong.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 8, time = 1)
@Measurement(iterations = 8, time = 1)
@Fork(2)
public class HollowObjectTypeReadStateLongBenchmark {

    public static class LongHolder {
        long value;
        LongHolder(long value) { this.value = value; }
    }

    @Param({ "1000000" })
    int countRecordsDb;

    @Param({ "1000000" })
    int countReads;

    // max encoded bits: 40 -> fast path (<=56), 62 -> large path (>56)
    @Param({ "40", "62" })
    int maxBits;

    HollowObjectTypeDataAccess dataAccess;
    int[] readOrder;

    @Setup
    public void setUp() throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(LongHolder.class);

        Random r = new Random(42);
        // Zig-zag encoding roughly doubles magnitude, so cap at maxBits-1 raw to land around maxBits encoded.
        long bound = 1L << Math.max(1, maxBits - 1);
        for (int i = 0; i < countRecordsDb; i++) {
            long v = (r.nextLong() & (bound - 1));
            objectMapper.add(new LongHolder(v));
        }

        readOrder = new int[countReads];
        for (int i = 0; i < countReads; i++) {
            readOrder[i] = r.nextInt(countRecordsDb);
        }

        HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);
        dataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("LongHolder", 0);
    }

    @Benchmark
    public void readLong(Blackhole bh) {
        HollowObjectTypeDataAccess da = dataAccess;
        int[] order = readOrder;
        long sum = 0;
        for (int i = 0; i < order.length; i++) {
            sum += da.readLong(order[i], 0);
        }
        bh.consume(sum);
    }
}
