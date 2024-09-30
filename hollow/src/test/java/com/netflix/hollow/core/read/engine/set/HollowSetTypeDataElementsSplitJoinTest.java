package com.netflix.hollow.core.read.engine.set;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Test;

public class HollowSetTypeDataElementsSplitJoinTest extends AbstractHollowSetTypeDataElementsSplitJoinTest {

    @Test
    public void testSplitThenJoin() throws IOException {
        int maxNumMapRecords = 100;

        // 1->2->1, 1->4->1, ...
        for (int numRecords=0;numRecords<maxNumMapRecords;numRecords++) {
            int[][] sets = generateSetContents(numRecords);
            HollowSetTypeReadState typeReadState = populateTypeStateWith(sets);
            assertEquals(1, typeReadState.numShards());
            assertEquals(sets.length, typeReadState.getPopulatedOrdinals().cardinality());
            assertDataUnchanged(typeReadState,sets);

            for (int numSplits : new int[]{2}) {  // 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
                HollowSetTypeDataElementsSplitter splitter = new HollowSetTypeDataElementsSplitter(typeReadState.currentDataElements()[0], numSplits);
                HollowSetTypeDataElements[] splitElements = splitter.split();

                HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(splitElements);
                HollowSetTypeDataElements joinedElements = joiner.join();

                HollowSetTypeReadState resultTypeReadState = new HollowSetTypeReadState(typeReadState.getSchema(), joinedElements);
                assertDataUnchanged(resultTypeReadState, sets);
                assertChecksumUnchanged(resultTypeReadState, typeReadState, typeReadState.getPopulatedOrdinals());
            }
        }
    }

    @Test
    public void testSplitThenJoinWithEmptyJoin() throws IOException {
        int[][] sets = new int[][] {
                {1, 1}
        };
        HollowSetTypeReadState typeReadState = populateTypeStateWith(sets);
        assertEquals(1, typeReadState.numShards());

        HollowSetTypeDataElementsSplitter splitter = new HollowSetTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 4);
        HollowSetTypeDataElements[] splitBy4 = splitter.split();
        assertEquals(-1, splitBy4[1].maxOrdinal);
        assertEquals(-1, splitBy4[3].maxOrdinal);

        HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(new HollowSetTypeDataElements[]{splitBy4[1], splitBy4[3]});
        HollowSetTypeDataElements joined = joiner.join();

        assertEquals(-1, joined.maxOrdinal);
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
