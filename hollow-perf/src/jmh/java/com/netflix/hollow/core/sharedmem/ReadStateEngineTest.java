package com.netflix.hollow.core.sharedmem;

import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.api.gen.topn.SetOfTopNAttribute;
import com.netflix.hollow.core.api.gen.topn.TopN;
import com.netflix.hollow.core.api.gen.topn.TopNAPI;
import com.netflix.hollow.core.api.gen.topn.TopNAttribute;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.BitSet;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
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

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 500, time = 1)
@Fork(1)
public class ReadStateEngineTest {

    @Param( {"1000"})
    private int n;

    private static final String TEST_FILE_TOPN = "/Users/sunjeets/workspace/onboarding/topN";
    public static final String TEST_FILE_FEATHER_OVERRIDE = "/Users/sunjeets/workspace/onboarding/vms-feather_override";


    private HollowReadStateEngine readState;

    @Setup
    public void setUp() throws IOException {

        readState = new HollowReadStateEngine();
        HollowBlobReader fileReader = new HollowBlobReader(readState);
        RandomAccessFile raf = new RandomAccessFile(TEST_FILE_TOPN, "r");
        FileChannel channel = raf.getChannel(); // Map MappedByteBuffer once, pass it everywhere
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
        BufferedWriter debug = new BufferedWriter(new FileWriter("/tmp/debug_new"));
        fileReader.readSnapshot(raf, buffer, debug);
        debug.flush();
        System.out.println("SNAP: Done setup");
    }

    public void referenceReadState(HollowReadStateEngine readState) {
        referenceType("TopN");
        // referenceType("TopNAttribute");
        // referenceType("SetOfTopNAttribute");
        // referenceType("String");

    }

    public void referenceType(String type) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readState.getTypeState(type);

        BitSet populatedOrdinals = typeState.getPopulatedOrdinals();
        int ordinal = populatedOrdinals.nextSetBit(0);
        while (ordinal != -1) {
            GenericHollowObject obj = new GenericHollowObject(new HollowObjectGenericDelegate(typeState), ordinal);
            obj.getOrdinal();   // some operation on object
            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }

        TopNAPI topNAPI = new TopNAPI(readState);
        for (TopN topN : topNAPI.getAllTopN()) {
            SetOfTopNAttribute attributes = topN.getAttributes();
            int videoId = (int) topN.getVideoId();
            for (TopNAttribute topNAttribute : attributes) {
                String countryId = topNAttribute.getCountry();
                if (videoId == countryId.hashCode()) {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    @Benchmark
    public void scan() {
        referenceReadState(readState);
    }

}

/***
 * now averaging 235 ms / op as opposed to 150ms ms /op with vanilla implementation
 */
