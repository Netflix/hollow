package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.BitSet;
import org.junit.Test;

public class HollowListTypeDataElementsSplitJoinTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    @Test
    public void testSplitThenJoin() throws IOException {

        int numListRecords = 100;
        int[][] listContents = new int[numListRecords][];
        for (int i=0;i<numListRecords;i++) {
            listContents[i] = new int[i+1];
            for (int j=0;j<i+1;j++) {
                listContents[i][j] = j;
            }
        }

        // 1->2->1, 1->4->1, ...
        for (int listRecord=0;listRecord<numListRecords;listRecord++) {
            HollowListTypeReadState typeReadState = populateTypeStateWith(listContents);
            assertEquals(1, typeReadState.numShards());
            assertEquals(numListRecords, typeReadState.getPopulatedOrdinals().cardinality());
            assertDataUnchanged(typeReadState, listContents);

            for (int numSplits : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
                HollowListTypeDataElementsSplitter splitter = new HollowListTypeDataElementsSplitter(typeReadState.currentDataElements()[0], numSplits);
                HollowListTypeDataElements[] splitElements = splitter.split();

                HollowListTypeDataElementsJoiner joiner = new HollowListTypeDataElementsJoiner(splitElements);
                HollowListTypeDataElements joinedElements = joiner.join();

                HollowListTypeReadState resultTypeReadState = new HollowListTypeReadState(typeReadState.getSchema(), joinedElements);
                assertDataUnchanged(resultTypeReadState, listContents);
                assertChecksumUnchanged(resultTypeReadState, typeReadState, typeReadState.getPopulatedOrdinals());
            }
        }
    }


    @Test
    public void testSplitThenJoinWithEmptyJoin() throws IOException {
        int numListRecords = 1;
        int[][] listContents = {{1}};
        HollowListTypeReadState typeReadState = populateTypeStateWith(listContents);
        assertEquals(1, typeReadState.numShards());

        HollowListTypeDataElementsSplitter splitter = new HollowListTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 4);
        HollowListTypeDataElements[] splitBy4 = splitter.split();
        assertEquals(-1, splitBy4[1].maxOrdinal);
        assertEquals(-1, splitBy4[3].maxOrdinal);

        HollowListTypeDataElementsJoiner joiner = new HollowListTypeDataElementsJoiner(new HollowListTypeDataElements[]{splitBy4[1], splitBy4[3]});
        HollowListTypeDataElements joined = joiner.join();

        assertEquals(-1, joined.maxOrdinal);
    }

    // manually invoked
    // @Test
    public void testSplittingAndJoiningWithSnapshotBlob() throws Exception {

        String blobPath = null; // dir where snapshot blob exists for e.g. "/tmp/";
        long v = 0l; // snapshot version for e.g. 20230915162636001l;
        String[] listTypesWithOneShard = null; // type name corresponding to an Object type with single shard for e.g. "Movie";
        int[] numSplitsArray = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024};

        HollowFilesystemBlobRetriever hollowBlobRetriever = new HollowFilesystemBlobRetriever(Paths.get(blobPath));
        HollowConsumer c = HollowConsumer.withBlobRetriever(hollowBlobRetriever).build();
        c.triggerRefreshTo(v);
        HollowReadStateEngine readStateEngine = c.getStateEngine();

        for (String listTypeWithOneShard : listTypesWithOneShard) {
            for (int numSplits : numSplitsArray) {
                if (blobPath==null || v==0l || listTypeWithOneShard==null) {
                    throw new IllegalArgumentException("These arguments need to be specified");
                }
                HollowListTypeReadState typeState = (HollowListTypeReadState) readStateEngine.getTypeState(listTypeWithOneShard);

                assertEquals(1, typeState.numShards());

                HollowListTypeDataElementsSplitter splitter = new HollowListTypeDataElementsSplitter(typeState.currentDataElements()[0], numSplits);
                HollowListTypeDataElements[] splitElements = splitter.split();

                HollowListTypeDataElementsJoiner joiner = new HollowListTypeDataElementsJoiner(splitElements);
                HollowListTypeDataElements joinedElements = joiner.join();

                HollowListTypeReadState resultTypeState = new HollowListTypeReadState(typeState.getSchema(), joinedElements);
                assertChecksumUnchanged(resultTypeState, typeState, typeState.getPopulatedOrdinals());

                System.out.println("Processed type " + listTypeWithOneShard + " with " + numSplits + " splits");
            }
        }
    }

    private void assertChecksumUnchanged(HollowListTypeReadState newTypeState, HollowListTypeReadState origTypeState, BitSet populatedOrdinals) {
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
