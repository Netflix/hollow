package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.BitSet;
import org.junit.Test;

public class HollowMapTypeDataElementsSplitJoinTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {

    @Test
    public void testSplitThenJoin() throws IOException {
        int[][][] maps = new int[][][] {
                { {33321, 1}, {2, 2}, {32224, 3} },
                { {1, 31442}, {2, 1}, {3, 2} },
                { {1002, 2} },
                { {0, 134} },
        };

        // 1->2->1, 1->4->1, ...
        for (int listRecord=0;listRecord<maps.length;listRecord++) {
            HollowMapTypeReadState typeReadState = populateTypeStateWith(maps);
            assertEquals(1, typeReadState.numShards());
            assertEquals(maps.length, typeReadState.getPopulatedOrdinals().cardinality());
            assertDataUnchanged(typeReadState,maps);

            for (int numSplits : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
                HollowMapTypeDataElementsSplitter splitter = new HollowMapTypeDataElementsSplitter(typeReadState.currentDataElements()[0], numSplits);
                HollowMapTypeDataElements[] splitElements = splitter.split();

                HollowMapTypeDataElementsJoiner joiner = new HollowMapTypeDataElementsJoiner(splitElements);
                HollowMapTypeDataElements joinedElements = joiner.join();

                HollowMapTypeReadState resultTypeReadState = new HollowMapTypeReadState(typeReadState.getSchema(), joinedElements);
                assertDataUnchanged(resultTypeReadState, maps);
                assertChecksumUnchanged(resultTypeReadState, typeReadState, typeReadState.getPopulatedOrdinals());
            }
        }
    }

    @Test
    public void testSplitThenJoinWithEmptyJoin() throws IOException {
        int[][][] maps = new int[][][] {
                { {1, 1} }
        };
        HollowMapTypeReadState typeReadState = populateTypeStateWith(maps);
        assertEquals(1, typeReadState.numShards());

        HollowMapTypeDataElementsSplitter splitter = new HollowMapTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 4);
        HollowMapTypeDataElements[] splitBy4 = splitter.split();
        assertEquals(-1, splitBy4[1].maxOrdinal);
        assertEquals(-1, splitBy4[3].maxOrdinal);

        HollowMapTypeDataElementsJoiner joiner = new HollowMapTypeDataElementsJoiner(new HollowMapTypeDataElements[]{splitBy4[1], splitBy4[3]});
        HollowMapTypeDataElements joined = joiner.join();

        assertEquals(-1, joined.maxOrdinal);
    }

    // manually invoked
    // @Test
    public void testSplittingAndJoiningWithSnapshotBlob() throws Exception {

        String blobPath = null; // dir where snapshot blob exists for e.g. "/tmp/";
        long v = 0l; // snapshot version for e.g. 20230915162636001l;
        String[] mapTypesWithOneShard = null; // type name corresponding to an Object type with single shard for e.g. "Movie";
        int[] numSplitsArray = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};

        HollowFilesystemBlobRetriever hollowBlobRetriever = new HollowFilesystemBlobRetriever(Paths.get(blobPath));
        HollowConsumer c = HollowConsumer.withBlobRetriever(hollowBlobRetriever).build();
        c.triggerRefreshTo(v);
        HollowReadStateEngine readStateEngine = c.getStateEngine();

        for (String mapTypeWithOneShard : mapTypesWithOneShard) {
            for (int numSplits : numSplitsArray) {
                if (blobPath==null || v==0l || mapTypeWithOneShard==null) {
                    throw new IllegalArgumentException("These arguments need to be specified");
                }
                HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState(mapTypeWithOneShard);

                assertEquals(1, typeState.numShards());

                HollowMapTypeDataElementsSplitter splitter = new HollowMapTypeDataElementsSplitter(typeState.currentDataElements()[0], numSplits);
                HollowMapTypeDataElements[] splitElements = splitter.split();

                HollowMapTypeDataElementsJoiner joiner = new HollowMapTypeDataElementsJoiner(splitElements);
                HollowMapTypeDataElements joinedElements = joiner.join();

                HollowMapTypeReadState resultTypeState = new HollowMapTypeReadState(typeState.getSchema(), joinedElements);
                assertChecksumUnchanged(resultTypeState, typeState, typeState.getPopulatedOrdinals());

                System.out.println("Processed type " + mapTypeWithOneShard + " with " + numSplits + " splits");
            }
        }
    }

    private void assertChecksumUnchanged(HollowMapTypeReadState newTypeState, HollowMapTypeReadState origTypeState, BitSet populatedOrdinals) {
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
