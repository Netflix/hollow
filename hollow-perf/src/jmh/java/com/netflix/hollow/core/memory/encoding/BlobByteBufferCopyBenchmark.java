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
package com.netflix.hollow.core.memory.encoding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
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
public class BlobByteBufferCopyBenchmark {

    @Param({ "8", "32", "64", "512", "2048" })
    int length;

    @Param({ "128", "4089" })
    int start;

    BlobByteBuffer data;

    @Setup
    public void setUp() throws Exception {
        byte[] bytes = new byte[8192];
        for(int i = 0; i < bytes.length; i++)
            bytes[i] = (byte)(i * 31);

        File file = File.createTempFile("blob-byte-buffer-copy", ".bin");
        file.deleteOnExit();
        try(FileOutputStream out = new FileOutputStream(file)) {
            out.write(bytes);
        }
        try(FileInputStream in = new FileInputStream(file);
            FileChannel channel = in.getChannel()) {
            data = BlobByteBuffer.mmapBlob(channel, 4096);
        }
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
            result[i] = data.getByte(start + i);
        return result;
    }
}
