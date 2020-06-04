package com.netflix.hollow.core.sharedmem;

import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.api.gen.topn.SetOfTopNAttribute;
import com.netflix.hollow.core.api.gen.topn.TopN;
import com.netflix.hollow.core.api.gen.topn.TopNAPI;
import com.netflix.hollow.core.api.gen.topn.TopNAttribute;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
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
        HollowBlobInput in = HollowBlobInput.randomAccess(new File(TEST_FILE_TOPN));
        fileReader.readSnapshot(in);
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
 * Flattening SegmentedLong and SegmentedByte arrays to BlobByteBuffers, averaging (with optimization) 145 ms / op as opposed to 150ms ms /op with vanilla implementation
 *
 * ReadStateEngineTest.scan  1000  avgt  500  145.031 ± 0.620  ms/op
 */
