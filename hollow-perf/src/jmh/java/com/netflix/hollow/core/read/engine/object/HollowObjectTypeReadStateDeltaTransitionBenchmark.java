package com.netflix.hollow.core.read.engine.object;


import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
@BenchmarkMode({Mode.All})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 7, time = 1)
@Measurement(iterations = 7, time = 1)
@Fork(1)
/**
 * Runs delta transitions in the background while benchmarking reads. Re-sharding in delta transitions can be toggled with a param.
 */
public class HollowObjectTypeReadStateDeltaTransitionBenchmark {
    HollowWriteStateEngine writeStateEngine;
    HollowReadStateEngine readStateEngine;
    HollowObjectTypeDataAccess dataAccess;
    HollowObjectMapper objectMapper;

    @Param({ "500" })
    int countStrings;

    @Param({ "2000" })
    int deltaChanges;

    @Param({ "true" })
    boolean isReshardingEnabled;

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

    @Setup
    public void setUp() throws ExecutionException, InterruptedException {
        final List<String> readStrings = new ArrayList<>();
        final Set<Integer> readKeys = new HashSet<>();
        refreshExecutor = Executors.newSingleThreadExecutor();

        refreshExecutor.submit(() -> {
            Random r = new Random();
            writeStateEngine = new HollowWriteStateEngine();
            writeStateEngine.setTargetMaxTypeShardSize((long) shardSizeMBs * 1000l * 1000l);
            objectMapper = new HollowObjectMapper(writeStateEngine);
            objectMapper.initializeTypeState(String.class);

            readOrder = new ArrayList<>(countStrings);
            for (int i = 0; i < countStrings; i++) {
                readOrder.add(r.nextInt(countStringsDb));
            }
            readKeys.addAll(readOrder);

            for (int i = 0; i < countStringsDb; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append("string_");
                sb.append(i);
                sb.append("_");
                int thisStringLength = r.nextInt(maxStringLength) - sb.length() + 1;
                for (int j = 0; j < thisStringLength; j++) {
                    sb.append((char) (r.nextInt(26) + 'a'));
                }
                String s = sb.toString();
                objectMapper.add(s);
                if (readKeys.contains(i)) {
                    readStrings.add(s);
                }
            }

            readStateEngine = new HollowReadStateEngine();
            try {
                StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            dataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("String", 0);
        }).get();

        doneBenchmark = new CountDownLatch(1);
        reshardingFuture = refreshExecutor.submit(() -> {
            Random r = new Random();
            long origShardSize = shardSizeMBs * 1000 * 1000;
            long newShardSize = origShardSize;
            do {
                for (int i=0; i<readStrings.size(); i++) {
                    objectMapper.add(readStrings.get(i));
                }
                for (int i = 0; i < deltaChanges; i++) {
                    int changeKey = r.nextInt(countStringsDb);
                    if (readKeys.contains(changeKey)) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("string_");
                    sb.append(changeKey);
                    sb.append("_");
                    int thisStringLength = r.nextInt(maxStringLength) - sb.length() + 1;
                    for (int j = 0; j < thisStringLength; j++) {
                        sb.append((char) (r.nextInt(26) + 'a'));
                    }
                    objectMapper.add(sb.toString());
                }

                try {
                    if (isReshardingEnabled) {
                        if (newShardSize == origShardSize) {
                            newShardSize = origShardSize / 10;
                        } else {
                            newShardSize = origShardSize;
                        }
                        writeStateEngine.setTargetMaxTypeShardSize(newShardSize);
                    }
                    StateEngineRoundTripper.roundTripDelta(writeStateEngine, readStateEngine);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } while (doneBenchmark.getCount() > 0);
        });
    }

    @TearDown
    public void tearDown() {
        doneBenchmark.countDown();
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
    public void testReadString(Blackhole bh) {
        for (int j : readOrder) {
            String result = dataAccess.readString(j, 0);
            bh.consume(result);
        }
    }
}
