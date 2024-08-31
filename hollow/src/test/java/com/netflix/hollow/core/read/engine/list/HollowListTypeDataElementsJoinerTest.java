package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowListTypeDataElementsJoinerTest extends AbstractHollowListTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.setTargetMaxTypeShardSize(16);
    }

    @Test
    public void testJoin() throws IOException {
        int[][] listContents = new int[][] {
                {1000, 2000, 3000}};
        HollowListTypeReadState typeReadState = populateTypeStateWith(listContents);
        assertEquals(1, typeReadState.numShards());

        listContents = new int[][] {
                {1000, 2000, 3000},
                {0},
                {}
        };
        int listLen = 50;
        listContents[2] = new int[listLen];
        for (int i=0; i<listLen; i++) {
            listContents[2][i] = (int) Math.pow(2,7) - i;
        }
        HollowListTypeReadState typeReadStateSharded = populateTypeStateWith(listContents);
        assertDataUnchanged(typeReadStateSharded, listContents);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowListTypeDataElementsJoiner joiner = new HollowListTypeDataElementsJoiner(typeReadStateSharded.currentDataElements());
        HollowListTypeDataElements joinedDataElements = joiner.join();

        HollowListTypeReadStateShard joinedShard = new HollowListTypeReadStateShard();
        joinedShard.setCurrentData(joinedDataElements);
        typeReadState = new HollowListTypeReadState(MemoryMode.ON_HEAP, typeReadState.getSchema(), new HollowListTypeReadStateShard[] {joinedShard});
        assertDataUnchanged(typeReadState, listContents);

        try {
            joiner = new HollowListTypeDataElementsJoiner(mockListTypeState.currentDataElements());
            joiner.join();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }

    @Test
    public void testJoinDifferentFieldWidths() throws IOException {
        HollowListTypeReadState typeReadStateSmall = populateTypeStateWith(new int[][] {{1}});
        assertEquals(1, typeReadStateSmall.numShards());
        HollowListTypeDataElements dataElementsSmall = typeReadStateSmall.currentDataElements()[0];
        int widthSmall = dataElementsSmall.bitsPerElement;
        long valSmall = dataElementsSmall.elementData.getElementValue(0, widthSmall);


        int bigListLen = 5;
        int[][] bigListContents = new int[3][bigListLen];
        for (int i=0; i<bigListLen; i++) {
            bigListContents[2][i] = (int) Math.pow(2,7) - i;
        }

        HollowListTypeReadState typeReadStateBig = populateTypeStateWith(bigListContents);
        assertEquals(1, typeReadStateBig.numShards());
        HollowListTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int widthBig = dataElementsBig.bitsPerElement;

        assertTrue(widthBig > widthSmall);

        HollowListTypeDataElementsJoiner joiner = new HollowListTypeDataElementsJoiner(new HollowListTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        HollowListTypeDataElements dataElementsJoined = joiner.join();
        int widthJoined = dataElementsJoined.bitsPerElement;

        long val0 = dataElementsJoined.elementData.getElementValue(0, widthJoined);

        assertEquals(widthBig, widthJoined);
        assertEquals(valSmall, val0);
    }

//    @Test
//    public void testLopsidedShards() {
//      // TODO: implement when producer allows enabling type sharding for List types
//    }
}
