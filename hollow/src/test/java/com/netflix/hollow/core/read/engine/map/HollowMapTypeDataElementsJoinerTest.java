package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapTypeDataElementsJoinerTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.setTargetMaxTypeShardSize(16);
    }

    @Test
    public void testJoin() throws IOException {
        int[][][] maps = new int[][][] { { {1, 1}, {2, 2}, {3, 3} } };
        HollowMapTypeReadState typeReadState = populateTypeStateWith(maps);
        assertEquals(1, typeReadState.numShards());

        maps = new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                {},
            };
        int entryLen = 20;
        maps[2] = new int[entryLen][2];
        for (int i=0; i<entryLen; i++) {
            maps[2][i][0] = (int) Math.pow(2,7) - i;
            maps[2][i][1] = (int) Math.pow(2,7) - i;
        }

        HollowMapTypeReadState typeReadStateSharded = populateTypeStateWith(maps);
        assertDataUnchanged(typeReadStateSharded, maps);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowMapTypeDataElementsJoiner joiner = new HollowMapTypeDataElementsJoiner(typeReadStateSharded.currentDataElements());
        HollowMapTypeDataElements joinedDataElements = joiner.join();

        typeReadState = new HollowMapTypeReadState(typeReadState.getSchema(), joinedDataElements);
        assertDataUnchanged(typeReadState, maps);

        try {
            joiner = new HollowMapTypeDataElementsJoiner(mockMapTypeState.currentDataElements());
            joiner.join();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }

    @Test
    public void testJoinDifferentFieldWidths() throws IOException {
        HollowMapTypeReadState typeReadStateSmall = populateTypeStateWith(new int[][][] {{{1,2}}});
        assertEquals(1, typeReadStateSmall.numShards());
        HollowMapTypeDataElements dataElementsSmall = typeReadStateSmall.currentDataElements()[0];
        int widthSmall = dataElementsSmall.bitsPerMapEntry;
        long keySmall = dataElementsSmall.entryData.getElementValue(0, dataElementsSmall.bitsPerKeyElement);
        long valSmall = dataElementsSmall.entryData.getElementValue(0 + dataElementsSmall.bitsPerKeyElement, dataElementsSmall.bitsPerValueElement);
        assertEquals(1, keySmall);
        assertEquals(2, valSmall);

        int[][][] bigValueMapOriginal = new int[][][] {
                { {1000, 2000} },
                { {1000, 4000} }
        };

        HollowMapTypeReadState typeReadStateBig = populateTypeStateWith(bigValueMapOriginal);
        assertEquals(1, typeReadStateBig.numShards());
        HollowMapTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int widthBig = dataElementsBig.bitsPerMapEntry;

        assertTrue(widthBig > widthSmall);

        HollowMapTypeDataElementsJoiner joiner = new HollowMapTypeDataElementsJoiner(new HollowMapTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        HollowMapTypeDataElements dataElementsJoined = joiner.join();
        int widthJoined = dataElementsJoined.bitsPerMapEntry;

        long keyJoined = dataElementsJoined.entryData.getElementValue(0, dataElementsJoined.bitsPerKeyElement);
        long valJoined = dataElementsJoined.entryData.getElementValue(0 + dataElementsJoined.bitsPerKeyElement, dataElementsJoined.bitsPerValueElement);

        assertEquals(widthBig, widthJoined);
        assertEquals(keySmall, keyJoined);
        assertEquals(valSmall, valJoined);

        int ordinalFirstBig = 1;
        long startBucketFirstBig = dataElementsJoined.getStartBucket(ordinalFirstBig);
        long endBucketFirstBig = dataElementsJoined.getEndBucket(ordinalFirstBig);
        Map<Integer, Integer> bigValueMapJoined = new HashMap<>();
        for (long bucket=startBucketFirstBig;bucket<endBucketFirstBig;bucket++) {
            long key = dataElementsJoined.entryData.getElementValue(bucket * dataElementsJoined.bitsPerMapEntry, dataElementsJoined.bitsPerKeyElement);
            if (key == dataElementsJoined.emptyBucketKeyValue) {
                continue;
            }
            long value = dataElementsJoined.entryData.getElementValue((bucket * dataElementsJoined.bitsPerMapEntry) + dataElementsJoined.bitsPerKeyElement, dataElementsJoined.bitsPerValueElement);
            bigValueMapJoined.put((int) key, (int) value);
        }

        Map<Integer, Integer> expected = new HashMap<Integer, Integer>() {{ put(bigValueMapOriginal[0][0][0], bigValueMapOriginal[0][0][1]); }};
        assertEquals(expected, bigValueMapJoined);
    }

//    @Test
//    public void testLopsidedShards() {
//      // SNAP: TODO: implement when producer supports enabling type sharding for Map types
//    }
}
