package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.encoding.VarInt;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
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
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 20, time = 1)
@Fork(5)
public class HollowObjectTypeReadStateShardBenchmark {
    HollowWriteStateEngine writeStateEngine;
    HollowReadStateEngine readStateEngine;
    HollowObjectTypeDataAccess dataAccess;
    HollowObjectMapper objectMapper;

    @Param({ "500" })
    int countStrings;

    @Param({ "100000" })
    int countStringsDb;

    int[] readOrder;

    @Param({ "5", "25", "50", "90", "150", "1000" })
    int maxStringLength;

    @Param({ "0", "5", "10", "50", "90" })
    int probabilityUnicode;

    @Param({"false", "true"})
    boolean useOptimization;

    @Setup
    public void setUp() throws IOException {
        VarInt.USE_OPTIMIZATION = useOptimization;
        writeStateEngine = new HollowWriteStateEngine();
        objectMapper = new HollowObjectMapper(writeStateEngine);
        objectMapper.initializeTypeState(String.class);

        Random r = new Random();
        for (int i = 0; i < countStringsDb; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("string_");
            sb.append(i);
            sb.append('_');
            int thisStringLength = r.nextInt(maxStringLength) - sb.length() + 1;
            for (int j = 0; j < thisStringLength; j++) {
                if (r.nextInt(100) < probabilityUnicode) {
                    sb.append("\u123E");
                } else {
                    sb.append((char) (r.nextInt(26) + 'a'));
                }
            }
            objectMapper.add(sb.toString());
        }

        readOrder = new int[countStrings];
        for (int i = 0; i < countStrings; i++) {
            readOrder[i] = r.nextInt(countStringsDb);
        }

        readStateEngine = new HollowReadStateEngine();

        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);
        dataAccess = (HollowObjectTypeDataAccess) readStateEngine.getTypeDataAccess("String", 0);
    }

    @Benchmark
    public void testReadString(Blackhole bh) {
        for (int j : readOrder) {
            String result = dataAccess.readString(j, 0);
            //System.out.println(result);
            bh.consume(result);
        }
    }
}
