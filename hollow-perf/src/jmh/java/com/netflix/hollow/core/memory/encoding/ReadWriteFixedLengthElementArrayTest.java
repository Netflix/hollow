package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import hollow.FixedLengthElementArrayPlainPut;
import java.util.concurrent.ThreadLocalRandom;
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
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class ReadWriteFixedLengthElementArrayTest {

    @Param( {"4096"})
    private int bitSize;

    @Param( {"32"})
    private int bitsPerElement;

    @Param( {"1"})
    private int bitsPerStep;

    FixedLengthElementArray f;
    FixedLengthElementArrayPlainPut fm;

    int value;

    @Setup
    public void setUp() {
        f = new FixedLengthElementArray(WastefulRecycler.DEFAULT_INSTANCE, bitSize);
        fm = new FixedLengthElementArrayPlainPut(WastefulRecycler.DEFAULT_INSTANCE, bitSize);
        value = ThreadLocalRandom.current().nextInt(0, 1 << bitsPerElement);
    }

    // Reads

    @Benchmark
    public int read() {
        int sum = 0;
        for (int i = 0; i < (bitSize - bitsPerElement); i++) {
            sum += f.getElementValue(i, bitsPerElement);
        }
        return sum;
    }

    @Benchmark
    public int readLarge() {
        int sum = 0;
        for (int i = 0; i < (bitSize - bitsPerElement); i++) {
            sum += f.getLargeElementValue(i, bitsPerElement);
        }
        return sum;
    }

    // Writes

    @Benchmark
    public Object writeOrdered() {
        int sum = 0;
        for (int i = 0; i < (bitSize - bitsPerElement); i += bitsPerStep) {
            f.setElementValue(i, bitsPerElement, value);
        }
        return f;
    }

    @Benchmark
    public Object writePlain() {
        int sum = 0;
        for (int i = 0; i < (bitSize - bitsPerElement); i += bitsPerStep) {
            fm.setElementValue(i, bitsPerElement, value);
        }
        return f;
    }
}

/*
# JMH version: 1.21-SNAPSHOT
# VM version: JDK 1.8.0_181, VM 25.181-b13
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/jre/bin/java
# VM options: -XX:-TieredCompilation
...

Benchmark                                            (bitSize)  (bitsPerElement)  (bitsPerStep)  Mode  Cnt      Score      Error  Units
ReadWriteFixedLengthElementArrayTest.read                 4096                 1              1  avgt    5   6868.944 ±  133.584  ns/op
ReadWriteFixedLengthElementArrayTest.read                 4096                 8              1  avgt    5   6885.112 ±   63.520  ns/op
ReadWriteFixedLengthElementArrayTest.read                 4096                32              1  avgt    5   6840.659 ±   82.540  ns/op
ReadWriteFixedLengthElementArrayTest.read                 4096                60              1  avgt    5   6771.069 ±  153.265  ns/op
ReadWriteFixedLengthElementArrayTest.readLarge            4096                 1              1  avgt    5   9484.960 ±  532.618  ns/op
ReadWriteFixedLengthElementArrayTest.readLarge            4096                 8              1  avgt    5  10041.474 ±  813.135  ns/op
ReadWriteFixedLengthElementArrayTest.readLarge            4096                32              1  avgt    5  12186.423 ±  149.614  ns/op
ReadWriteFixedLengthElementArrayTest.readLarge            4096                60              1  avgt    5  14307.823 ±  842.377  ns/op

Benchmark                                          (bitSize)  (bitsPerElement)  (bitsPerStep)  Mode  Cnt      Score      Error  Units
ReadWriteFixedLengthElementArrayTest.writeOrdered       4096                 1              1  avgt    5  15967.409 ±  339.812  ns/op
ReadWriteFixedLengthElementArrayTest.writeOrdered       4096                 8              1  avgt    5  18288.946 ±  724.235  ns/op
ReadWriteFixedLengthElementArrayTest.writeOrdered       4096                32              1  avgt    5  21423.327 ± 1302.434  ns/op
ReadWriteFixedLengthElementArrayTest.writeOrdered       4096                60              1  avgt    5  25419.920 ± 1808.246  ns/op
ReadWriteFixedLengthElementArrayTest.writePlain         4096                 1              1  avgt    5  13209.819 ±  542.676  ns/op
ReadWriteFixedLengthElementArrayTest.writePlain         4096                 8              1  avgt    5  14119.386 ±  490.491  ns/op
ReadWriteFixedLengthElementArrayTest.writePlain         4096                32              1  avgt    5  17476.441 ± 1187.400  ns/op
ReadWriteFixedLengthElementArrayTest.writePlain         4096                60              1  avgt    5  21337.294 ±  448.718  ns/op


 */