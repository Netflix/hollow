package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.util.BitSet;
import org.junit.Test;

public class HollowMapTypeDataElementsSplitJoinTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {

    @Test
    public void testSplitThenJoin() throws IOException {
        int maxNumMapRecords = 100;

        // 1->2->1, 1->4->1, ...
        for (int numRecords=0;numRecords<maxNumMapRecords;numRecords++) {
            int[][][] maps = generateListContents(numRecords);
            HollowMapTypeReadState typeReadState = populateTypeStateWith(maps);
            assertEquals(1, typeReadState.numShards());
            assertEquals(maps.length, typeReadState.getPopulatedOrdinals().cardinality());
            assertDataUnchanged(typeReadState,maps);

            for (int numSplits : new int[]{2}) {  // 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024
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
