package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
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

/** Compares random fixed-length reads from contiguous and segmented on-heap storage. */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class FixedLengthDataReadBenchmark {

    @Param({"1000000"})
    int countRecords;

    @Param({"1000000"})
    int countReads;

    @Param({"40"})
    int bitsPerElement;

    FixedLengthData contiguous;
    FixedLengthData segmented;
    long[] indexes;
    long mask;

    @Setup
    public void setUp() {
        long numBits = (long) countRecords * bitsPerElement;
        contiguous = new ContiguousFixedLengthData(numBits);
        segmented = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, numBits);
        mask = (1L << bitsPerElement) - 1;

        Random random = new Random(42);
        for (int ordinal = 0; ordinal < countRecords; ordinal++) {
            long index = (long) ordinal * bitsPerElement;
            long value = random.nextLong() & mask;
            contiguous.setElementValue(index, bitsPerElement, value);
            segmented.setElementValue(index, bitsPerElement, value);
        }

        indexes = new long[countReads];
        for (int i = 0; i < countReads; i++) {
            indexes[i] = (long) random.nextInt(countRecords) * bitsPerElement;
        }
    }

    @Benchmark
    public long contiguous() {
        return read(contiguous);
    }

    @Benchmark
    public long segmented() {
        return read(segmented);
    }

    private long read(FixedLengthData data) {
        long[] readIndexes = indexes;
        int bits = bitsPerElement;
        long valueMask = mask;
        long sum = 0;
        for (long index : readIndexes) {
            sum += data.getElementValue(index, bits, valueMask);
        }
        return sum;
    }
}
