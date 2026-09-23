package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 6, time = 1)
@Fork(1)
public class SegmentedByteArrayCopyBenchmark {

    @Param({ "8", "32", "256", "2048" })
    int length;

    SegmentedByteArray data;
    long start;

    @Setup
    public void setUp() {
        data = new SegmentedByteArray(WastefulRecycler.DEFAULT_INSTANCE);
        start = (1 << WastefulRecycler.DEFAULT_INSTANCE.getLog2OfByteSegmentSize()) - 17;
        for(int i = 0; i < length; i++)
            data.set(start + i, (byte)(i * 31));
    }

    @Benchmark
    public byte[] copyTo() {
        byte[] result = new byte[length];
        data.copyTo(start, result, 0, length);
        return result;
    }

    @Benchmark
    public byte[] copyPerByte() {
        byte[] result = new byte[length];
        for(int i = 0; i < length; i++)
            result[i] = data.get(start + i);
        return result;
    }
}
