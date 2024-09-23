package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Test;

public class HollowListTypeDataElementsSplitJoinTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    @Test
    public void testSplitThenJoin() throws IOException {

        int maxNumListRecords = 100;

        // 1->2->1, 1->4->1, ...
        for (int numRecords=0;numRecords<maxNumListRecords;numRecords++) {

            int[][] listContents = generateListContents(numRecords);
            HollowListTypeReadState typeReadState = populateTypeStateWith(listContents);
            assertEquals(1, typeReadState.numShards());
            assertEquals(numRecords, typeReadState.getPopulatedOrdinals().cardinality());
            assertDataUnchanged(typeReadState, listContents);

            for (int numSplits : new int[]{2}) {  // 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
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
