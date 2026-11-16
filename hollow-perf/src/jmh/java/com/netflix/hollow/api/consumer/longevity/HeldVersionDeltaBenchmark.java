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
package com.netflix.hollow.api.consumer.longevity;

import com.netflix.hollow.api.consumer.HollowConsumer;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
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

/**
 * The cost of applying a delta in held mode: with no hold open, so nothing is copied; with a hold open behind the
 * engine, reading through the chain, so each delta's removed records are copied; and reading through direct lookup, so
 * each copy is also indexed. The differences are the per-delta cost of the copies and of indexing them. Each iteration
 * applies the next delta, which retitles one percent of the movies.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = HeldVersionDeltaBenchmark.WARMUP)
@Measurement(iterations = HeldVersionDeltaBenchmark.MEASURED)
@Fork(1)
public class HeldVersionDeltaBenchmark {
    static final int WARMUP = 20;
    static final int MEASURED = 100;

    /** Whether a hold is open behind the engine, and how it reads held versions. */
    @Param({"none", "chain", "direct"})
    public String copies;

    private HeldVersionsBench bench;
    private HollowConsumer consumer;
    private List<Long> versions;
    private int next;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        LogManager.getLogManager().reset();
        bench = new HeldVersionsBench();
        versions = bench.cycles(1 + WARMUP + MEASURED);
        consumer = bench.consumer(copies.equals("direct"));
        consumer.triggerRefreshTo(versions.get(0));
        if (!copies.equals("none"))
            consumer.holdVersion(versions.get(0));
        next = 1;
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        bench.delete();
    }

    /** Applies the next delta. */
    @Benchmark
    public long applyDelta() {
        consumer.triggerRefreshTo(versions.get(next++));
        return consumer.getCurrentVersionId();
    }
}
