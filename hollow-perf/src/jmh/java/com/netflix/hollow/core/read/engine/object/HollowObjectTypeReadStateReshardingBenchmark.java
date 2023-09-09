package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
@BenchmarkMode({Mode.All})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(1)
public class HollowObjectTypeReadStateReshardingBenchmark {
    HollowWriteStateEngine writeStateEngine;
    HollowReadStateEngine readStateEngine;
    HollowObjectTypeDataAccess dataAccess;
    HollowObjectMapper objectMapper;

    @Param({ "500" })
    int countStrings;

    @Param({ "100000" })
    int countStringsDb;

    ArrayList<Integer> readOrder;

    @Param({ "1" })
    int shardSizeMBs;

    @Param({ "5", "25", "50", "150", "1000" })
    int maxStringLength;

    ExecutorService refreshExecutor;
    Future<?> reshardingFuture;
    CountDownLatch doneBenchmark;

    AtomicLong counter;

    @Setup
    public void setUp() throws ExecutionException, InterruptedException {
        refreshExecutor = Executors.newSingleThreadExecutor();

        refreshExecutor.submit(() -> {
            writeStateEngine = new HollowWriteStateEngine();
            writeStateEngine.setTargetMaxTypeShardSize((long) shardSizeMBs * 1000l * 1000l);
            objectMapper = new HollowObjectMapper(writeStateEngine);
            objectMapper.initializeTypeState(String.class);

            Random r = new Random();
            for (int i = 0; i < countStringsDb; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append("string_");
                sb.append(i);
                sb.append("_");
                int thisStringLength = r.nextInt(maxStringLength) - sb.length() + 1;
                for (int j = 0; j < thisStringLength; j++) {
                    sb.append((char) (r.nextInt(26) + 'a'));
                }
                objectMapper.add(sb.toString());
            }

            readOrder = new ArrayList<>(countStrings);
            for (int i = 0; i < countStrings; i++) {
                readOrder.add(r.nextInt(countStringsDb));
            }

            readStateEngine = new HollowReadStateEngine();

            try {
                StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            dataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("String", 0);

        }).get();

        counter = new AtomicLong(0l);
        doneBenchmark = new CountDownLatch(1);
        reshardingFuture = refreshExecutor.submit(() -> {
            do {
                HollowObjectTypeReadState stringTypeState = (HollowObjectTypeReadState) dataAccess.getTypeState();
                stringTypeState.reshard(stringTypeState.numShards() * 2);
                stringTypeState.reshard(stringTypeState.numShards() / 2);
                counter.incrementAndGet();
            } while (doneBenchmark.getCount() > 0);
        });
    }

    @TearDown
    public void tearDown() {
        doneBenchmark.countDown();
        System.out.println("SNAP: Resharding done " + counter.get() + " times.");
        reshardingFuture.cancel(true);
        refreshExecutor.shutdown();
        try {
            if (!refreshExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                refreshExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            refreshExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Benchmark
    public void testReadString(Blackhole bh) throws ExecutionException, InterruptedException {
        for (int j : readOrder) {
            String result = dataAccess.readString(j, 0);
            //System.out.println(result);
            bh.consume(result);
        }
    }
}