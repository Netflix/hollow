package com.netflix.hollow.core.read.engine.set;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.nio.file.Paths;
import java.util.BitSet;

public class HollowSetTypeDataElementsSplitJoinTest extends AbstractHollowSetTypeDataElementsSplitJoinTest {

    // manually invoked
    // @Test
    public void testSplittingAndJoiningWithSnapshotBlob() throws Exception {

        String blobPath = null; // dir where snapshot blob exists for e.g. "/tmp/";
        long v = 0l; // snapshot version for e.g. 20230915162636001l;
        String[] setTypesWithOneShard = null; // type name corresponding to an Object type with single shard for e.g. "Movie";
        int[] numSplitsArray = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};

        HollowFilesystemBlobRetriever hollowBlobRetriever = new HollowFilesystemBlobRetriever(Paths.get(blobPath));
        HollowConsumer c = HollowConsumer.withBlobRetriever(hollowBlobRetriever).build();
        c.triggerRefreshTo(v);
        HollowReadStateEngine readStateEngine = c.getStateEngine();

        for (String setTypeWithOneShard : setTypesWithOneShard) {
            for (int numSplits : numSplitsArray) {
                if (blobPath==null || v==0l || setTypeWithOneShard==null) {
                    throw new IllegalArgumentException("These arguments need to be specified");
                }
                HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState(setTypeWithOneShard);

                assertEquals(1, typeState.numShards());

                HollowSetTypeDataElementsSplitter splitter = new HollowSetTypeDataElementsSplitter(typeState.currentDataElements()[0], numSplits);
                HollowSetTypeDataElements[] splitElements = splitter.split();

                HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(splitElements);
                HollowSetTypeDataElements joinedElements = joiner.join();

                HollowSetTypeReadState resultTypeState = new HollowSetTypeReadState(typeState.getSchema(), joinedElements);

                assertChecksumUnchanged(resultTypeState, typeState, typeState.getPopulatedOrdinals());

                System.out.println("Processed type " + setTypeWithOneShard + " with " + numSplits + " splits");
            }
        }
    }

    private void assertChecksumUnchanged(HollowSetTypeReadState newTypeState, HollowSetTypeReadState origTypeState, BitSet populatedOrdinals) {
        HollowChecksum origCksum = new HollowChecksum();
        HollowChecksum newCksum = new HollowChecksum();

        for(int i=0;i<origTypeState.numShards();i++) {
            origTypeState.shardsVolatile.shards[i].applyShardToChecksum(origCksum, populatedOrdinals, i, origTypeState.numShards());
        }

        for(int i=0;i<newTypeState.numShards();i++) {
            newTypeState.shardsVolatile.shards[i].applyShardToChecksum(newCksum, populatedOrdinals, i, newTypeState.numShards());
        }

        assertEquals(newCksum, origCksum);
    }
}
