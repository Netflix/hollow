/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.encoding.ContiguousFixedLengthData;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
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

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(3)
public class FixedLengthSnapshotReadBenchmark {
    @Param({"256", "131072", "1048576"})
    public int longs;

    @Param({"false", "true"})
    public boolean buffered;

    private byte[] bytes;

    @Setup
    public void setup() {
        bytes = new byte[longs * Long.BYTES];
        new Random(42).nextBytes(bytes);
    }

    private HollowBlobInput input() {
        ByteArrayInputStream source = new ByteArrayInputStream(bytes);
        return HollowBlobInput.serial(buffered ? new BufferedInputStream(source) : source);
    }

    @Benchmark
    public SegmentedLongArray segmented() throws IOException {
        try (HollowBlobInput input = input()) {
            SegmentedLongArray array = new SegmentedLongArray(WastefulRecycler.DEFAULT_INSTANCE, longs);
            array.readFrom(input, WastefulRecycler.DEFAULT_INSTANCE, longs);
            return array;
        }
    }

    @Benchmark
    public ContiguousFixedLengthData contiguous() throws IOException {
        try (HollowBlobInput input = input()) {
            return ContiguousFixedLengthData.newFrom(input, longs);
        }
    }
}
