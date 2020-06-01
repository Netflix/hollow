package com.netflix.hollow.core.read;

import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.SimultaneousExecutor;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class ReadWriteStateEngineTest {

    //@Param( {"1000", "10000", "100000", "1000000"})
    @Param( {"1000000"})
    private int n;

    private HollowWriteStateEngine writeEngine;
    private HollowObjectTypeWriteState typeWriteState;
    private HollowObjectSchema schema;

    static Logger BLOB_READER_LOGGER = Logger.getLogger("com.netflix.hollow.core.read.engine.HollowBlobReader");

    @Setup
    public void setUp() {
        BLOB_READER_LOGGER.setLevel(Level.OFF);

        this.writeEngine = new HollowWriteStateEngine();

        this.schema = new HollowObjectSchema("TestObject", 2);
        schema.addField("f1", HollowObjectSchema.FieldType.INT);
        schema.addField("f2", HollowObjectSchema.FieldType.STRING);

        this.typeWriteState = new HollowObjectTypeWriteState(schema);
        writeEngine.addTypeState(typeWriteState);

        for (int i = 0; i < n; i++) {
            addRecord(1, Integer.toString(i));
        }
    }

    private void addRecord(int f1, String f2) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        rec.setInt("f1", f1);
        rec.setString("f2", f2);
        writeEngine.add("TestObject", rec);
    }

    // Reads

    @Benchmark
    public HollowReadStateEngine roundtripPipe() {
        return roundTripSnapshotPipe(writeEngine);
    }

    @Benchmark
    public HollowReadStateEngine roundtripPipeBuffered() {
        return roundTripSnapshotPipeBuffered(writeEngine);
    }

    @Benchmark
    public HollowReadStateEngine roundtripMemory() throws IOException {
        return roundTripSnapshotMemory(writeEngine);
    }

    @Benchmark
    public HollowReadStateEngine roundtripFile() throws IOException {
        return roundTripSnapshotFile(writeEngine);
    }

    private static HollowReadStateEngine roundTripSnapshotPipe(HollowWriteStateEngine writeEngine) {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        HollowReadStateEngine removedRecordCopies = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(removedRecordCopies);

        // Use a pipe to write and read concurrently to avoid writing
        // to temporary files or allocating memory
        // @@@ for small states it's more efficient to sequentially write to
        // and read from a byte array but it is tricky to estimate the size
        SimultaneousExecutor executor = new SimultaneousExecutor(1, ReadWriteStateEngineTest.class, "test");
        Exception pipeException = null;
        // Ensure read-side is closed after completion of read
        try (PipedInputStream in = new PipedInputStream(1 << 15)) {
            PipedOutputStream out = new PipedOutputStream(in);
            executor.execute(() -> {
                // Ensure write-side is closed after completion of write
                try (Closeable ac = out) {
                    writer.writeSnapshot(out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            reader.readSnapshot(HollowBlobInput.serial(in));
        } catch (Exception e) {
            pipeException = e;
        }

        // Ensure no underlying writer exception is lost due to broken pipe
        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            if (pipeException == null) {
                throw new RuntimeException(e);
            }

            pipeException.addSuppressed(e);
        }
        if (pipeException != null) {
            throw new RuntimeException(pipeException);
        }

        return removedRecordCopies;
    }

    private static HollowReadStateEngine roundTripSnapshotPipeBuffered(HollowWriteStateEngine writeEngine) {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        HollowReadStateEngine removedRecordCopies = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(removedRecordCopies);

        // Use a pipe to write and read concurrently to avoid writing
        // to temporary files or allocating memory
        // @@@ for small states it's more efficient to sequentially write to
        // and read from a byte array but it is tricky to estimate the size
        SimultaneousExecutor executor = new SimultaneousExecutor(1, ReadWriteStateEngineTest.class, "test");
        Exception pipeException = null;
        // Ensure read-side is closed after completion of read
        try (PipedInputStream in = new PipedInputStream(1 << 15)) {
            PipedOutputStream out = new PipedOutputStream(in);
            executor.execute(() -> {
                // Ensure write-side is closed after completion of write
                try (Closeable ac = out) {
                    writer.writeSnapshot(new BufferedOutputStream(out));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            reader.readSnapshot(HollowBlobInput.serial(new BufferedInputStream(in)));
        } catch (Exception e) {
            pipeException = e;
        }

        // Ensure no underlying writer exception is lost due to broken pipe
        try {
            executor.awaitSuccessfulCompletion();
        } catch (InterruptedException | ExecutionException e) {
            if (pipeException == null) {
                throw new RuntimeException(e);
            }

            pipeException.addSuppressed(e);
        }
        if (pipeException != null) {
            throw new RuntimeException(pipeException);
        }

        return removedRecordCopies;
    }

    public static HollowReadStateEngine roundTripSnapshotMemory(HollowWriteStateEngine writeEngine) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writer.writeSnapshot(baos);

        reader.readSnapshot(HollowBlobInput.serial(baos.toByteArray()));

        return readEngine;
    }

    public static HollowReadStateEngine roundTripSnapshotFile(HollowWriteStateEngine writeEngine) throws IOException {
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        File f = File.createTempFile("snapshot", null);

        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f))) {
            writer.writeSnapshot(out);
            out.flush();
        }

        try (HollowBlobInput in = HollowBlobInput.serial(new BufferedInputStream(new FileInputStream(f)))) {
            reader.readSnapshot(in);
        }

        return readEngine;
    }
}


/*

Benchmark                                           (n)  Mode  Cnt    Score    Error  Units
ReadWriteStateEngineTest.roundtripFile             1000  avgt    5    0.543 ±  0.200  ms/op
ReadWriteStateEngineTest.roundtripFile            10000  avgt    5    1.838 ±  0.307  ms/op
ReadWriteStateEngineTest.roundtripFile           100000  avgt    5   13.867 ±  0.589  ms/op
ReadWriteStateEngineTest.roundtripFile          1000000  avgt    5  148.361 ± 12.882  ms/op
ReadWriteStateEngineTest.roundtripMemory           1000  avgt    5    0.165 ±  0.009  ms/op
ReadWriteStateEngineTest.roundtripMemory          10000  avgt    5    1.170 ±  0.168  ms/op
ReadWriteStateEngineTest.roundtripMemory         100000  avgt    5   11.469 ±  0.442  ms/op
ReadWriteStateEngineTest.roundtripMemory        1000000  avgt    5  126.531 ± 23.077  ms/op
ReadWriteStateEngineTest.roundtripPipe             1000  avgt    5    0.299 ±  0.016  ms/op
ReadWriteStateEngineTest.roundtripPipe            10000  avgt    5    1.709 ±  0.196  ms/op
ReadWriteStateEngineTest.roundtripPipe           100000  avgt    5   15.217 ±  0.255  ms/op
ReadWriteStateEngineTest.roundtripPipe          1000000  avgt    5  188.362 ± 17.491  ms/op
ReadWriteStateEngineTest.roundtripPipeBuffered     1000  avgt    5    0.257 ±  0.034  ms/op
ReadWriteStateEngineTest.roundtripPipeBuffered    10000  avgt    5    1.364 ±  0.188  ms/op
ReadWriteStateEngineTest.roundtripPipeBuffered   100000  avgt    5   11.595 ±  0.365  ms/op
ReadWriteStateEngineTest.roundtripPipeBuffered  1000000  avgt    5  122.227 ± 14.853  ms/op

# Large snapshot, about 100MB in size
ReadWriteStateEngineTest.roundtripFile          10000000  avgt    5  1418.340 ± 244.367  ms/op
ReadWriteStateEngineTest.roundtripMemory        10000000  avgt    5  1298.582 ± 149.978  ms/op
ReadWriteStateEngineTest.roundtripPipe          10000000  avgt    5  1748.863 ± 128.384  ms/op
ReadWriteStateEngineTest.roundtripPipeBuffered  10000000  avgt    5  1255.351 ± 123.275  ms/op

# Using buffering with the pipes will result in less calls to the piped streams
# Here is a stack sampling profile (using -p stack) for roundtripPipe

 20.7%  51.0% java.lang.Thread.isAlive
  9.7%  24.0% com.netflix.hollow.core.memory.SegmentedByteArray.copy
  6.4%  15.9% com.netflix.hollow.core.write.HollowObjectTypeWriteState.addRecord
  2.1%   5.3% com.netflix.hollow.core.memory.ByteArrayOrdinalMap.maxOrdinal
  0.6%   1.5% java.lang.Object.wait
  0.4%   1.0% com.netflix.hollow.core.write.HollowObjectTypeWriteState.calculateSnapshot
  0.1%   0.3% com.netflix.hollow.core.memory.pool.WastefulRecycler.getLongArray
  0.1%   0.1% com.netflix.hollow.core.memory.pool.RecyclingRecycler$1.create
  0.1%   0.1% com.netflix.hollow.core.memory.SegmentedByteArray.readFrom
  0.1%   0.1% java.lang.Object.notifyAll
  0.2%   0.6% <other>

# Here is a profile (using -p stack) for roundtripPipeBuffered

 12.4%  32.8% com.netflix.hollow.core.memory.SegmentedByteArray.copy
  8.6%  22.8% com.netflix.hollow.core.write.HollowObjectTypeWriteState.addRecord
  6.2%  16.3% java.lang.Thread.isAlive
  2.8%   7.4% com.netflix.hollow.core.memory.ByteArrayOrdinalMap.maxOrdinal
  2.0%   5.4% com.netflix.hollow.core.memory.SegmentedByteArray.readFrom
  1.9%   5.1% java.io.DataInputStream.readFully
  1.2%   3.2% java.io.DataOutputStream.writeLong
  0.9%   2.5% java.lang.Object.wait
  0.7%   1.8% com.netflix.hollow.core.write.HollowObjectTypeWriteState.calculateSnapshot
  0.3%   0.9% com.netflix.hollow.core.memory.SegmentedLongArray.readFrom
  0.8%   2.0% <other>

# We can observe that there are less calls to Thread.isAlive when buffering is performed,
# since more reads/writes are performed in bulk resulting in calls to the piped streams.
# Thread.isAlive is a native method and is not intrinsic to HotSpot (meaning there is no special
# treatment of this native call by the runtime compiler) therefore it is expensive to call (unlike that of say
# Thread.currentThread, which is intrinsic).  This likely explains the majority of the performance difference.
# Another aspect is likely contention with the reader thread waiting on the writer thread to write bytes and
# vice versa.
#
# Note: from OpenJDK 9 onwards most intrinsic methods are annotated with the @HotSpotIntrinsic annotation.

 */