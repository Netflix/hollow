/*
 *  Copyright 2026 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.tools.checksum;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.runner.IterationType;

/**
 * Compares the wall-clock time of a whole-engine integrity checksum in per-type mode ({@code perShard=false}) and
 * per-shard mode ({@code perShard=true}). This is the call the producer's integrity check makes four times per cycle.
 * <p>
 * Shapes:
 * <ul>
 *   <li>{@code lopsided}: one type holds 80% of the records, plus 10 small single-shard types. Per-type mode
 *       checksums the large type on one thread; per-shard mode spreads its shards across the pool.</li>
 *   <li>{@code balanced}: 16 equal types. Per-type mode already keeps the pool busy, so both modes should match.</li>
 * </ul>
 * {@code shards} is the shard count of the large type (lopsided) or of every type (balanced); {@code shards=1}
 * is the control, where per-shard mode has nothing to split.
 * <p>
 * The checksum pool is sized from {@code availableProcessors()}, so the fork pins it to 8 (an r5.2xlarge). Override
 * with {@code -jvmArgsAppend -XX:ActiveProcessorCount=N}.
 * <p>
 * After each trial, prints process CPU time per op and average busy cores (CPU time / wall time) over the
 * measurement iterations. Per-shard mode should cut wall time while CPU time per op stays about the same.
 * <p>
 * Run: {@code ./gradlew :hollow-perf:jmhJar && java -jar hollow-perf/build/libs/hollow-perf-*-jmh.jar
 * HollowChecksumModeBenchmark}
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5, time = 3)
@Fork(value = 1, jvmArgsAppend = {"-Xms6g", "-Xmx6g", "-XX:ActiveProcessorCount=8"})
public class HollowChecksumModeBenchmark {

    private static final com.sun.management.OperatingSystemMXBean OS =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @Param({"false", "true"})
    boolean perShard;

    @Param({"lopsided", "balanced"})
    String shape;

    @Param({"1", "8", "32"})
    int shards;

    @Param("3000000")
    int totalRecords;

    HollowReadStateEngine engine;

    private long iterationStartCpuNanos;
    private long iterationStartWallNanos;
    private long iterationOps;
    private long measuredCpuNanos;
    private long measuredWallNanos;
    private long measuredOps;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        switch (shape) {
            case "lopsided": {
                int bigRecords = totalRecords / 10 * 8;
                int smallRecords = (totalRecords - bigRecords) / 10;
                addType(writeEngine, "Big", bigRecords, shards);
                for (int i = 0; i < 10; i++)
                    addType(writeEngine, "Small" + i, smallRecords, 1);
                break;
            }
            case "balanced":
                for (int i = 0; i < 16; i++)
                    addType(writeEngine, "Even" + i, totalRecords / 16, shards);
                break;
            default:
                throw new IllegalArgumentException("Unknown shape " + shape);
        }
        engine = StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    private static void addType(HollowWriteStateEngine writeEngine, String name, int records, int numShards) {
        HollowObjectSchema schema = new HollowObjectSchema(name, 3);
        schema.addField("id", FieldType.LONG);
        schema.addField("count", FieldType.INT);
        schema.addField("guid", FieldType.STRING);
        writeEngine.addTypeState(new HollowObjectTypeWriteState(schema, numShards));

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < records; i++) {
            rec.reset();
            rec.setLong("id", i);
            rec.setInt("count", i & 0xFFFF);
            rec.setString("guid", new UUID(i, ~i).toString());
            writeEngine.add(name, rec);
        }
    }

    @Setup(Level.Iteration)
    public void startIteration() {
        iterationOps = 0;
        iterationStartCpuNanos = OS.getProcessCpuTime();
        iterationStartWallNanos = System.nanoTime();
    }

    @TearDown(Level.Iteration)
    public void endIteration(IterationParams params) {
        long wall = System.nanoTime() - iterationStartWallNanos;
        long cpu = OS.getProcessCpuTime() - iterationStartCpuNanos;
        if (params.getType() == IterationType.MEASUREMENT) {
            measuredWallNanos += wall;
            measuredCpuNanos += cpu;
            measuredOps += iterationOps;
        }
    }

    @TearDown(Level.Trial)
    public void report() {
        System.out.printf("%n[cpu] perShard=%s shape=%s shards=%d: %.1f cpu-ms/op, %.2f busy cores%n",
                perShard, shape, shards,
                measuredCpuNanos / 1e6 / Math.max(1, measuredOps),
                (double) measuredCpuNanos / Math.max(1, measuredWallNanos));
    }

    @Benchmark
    public int checksum() {
        iterationOps++;
        return HollowChecksum.forStateEngineWithCommonSchemas(engine, engine, perShard).intValue();
    }
}
