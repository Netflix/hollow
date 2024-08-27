package com.netflix.hollow.core.read.engine.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowSetTypeDataElementsJoinerTest extends AbstractHollowSetTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.setTargetMaxTypeShardSize(16);
    }

    @Test
    public void testJoin() throws IOException {
        int[][] setContents = new int[][] {
                {1000, 2000, 3000}};
        HollowSetTypeReadState typeReadState = populateTypeStateWith(setContents);
        assertEquals(1, typeReadState.numShards());

        setContents = new int[][] {
                {1000, 2000, 3000},
                {0},
                {}
        };
        int setSize = 50;
        setContents[2] = new int[setSize];
        for (int i=0; i<setSize; i++) {
            setContents[2][i] = (int) Math.pow(2,7) - i;
        }
        HollowSetTypeReadState typeReadStateSharded = populateTypeStateWith(setContents);
        assertDataUnchanged(typeReadStateSharded, setContents);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(typeReadStateSharded.currentDataElements());
        HollowSetTypeDataElements joinedDataElements = joiner.join();

        HollowSetTypeReadStateShard joinedShard = new HollowSetTypeReadStateShard();
        joinedShard.setCurrentData(joinedDataElements);
        typeReadState = new HollowSetTypeReadState(typeReadState.getStateEngine(), MemoryMode.ON_HEAP, typeReadState.getSchema(), 1,
                new HollowSetTypeReadStateShard[] {joinedShard});
        assertDataUnchanged(typeReadState, setContents);

        try {
            joiner = new HollowSetTypeDataElementsJoiner(mockListTypeState.currentDataElements());
            joiner.join();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }

    @Test
    public void testJoinDifferentFieldWidths() throws IOException {
        HollowSetTypeReadState typeReadStateSmall = populateTypeStateWith(new int[][] {{1}});
        assertEquals(1, typeReadStateSmall.numShards());
        HollowSetTypeDataElements dataElementsSmall = typeReadStateSmall.currentDataElements()[0];
        int widthSmall = dataElementsSmall.bitsPerElement;
        long valSmall = dataElementsSmall.elementData.getElementValue(0, widthSmall);

        int bigListLen = 5;
        int[][] bigListContents = new int[3][bigListLen];
        for (int i=0; i<bigListLen; i++) {
            bigListContents[2][i] = (int) Math.pow(2,7) - i;
        }

        HollowSetTypeReadState typeReadStateBig = populateTypeStateWith(bigListContents);
        assertEquals(1, typeReadStateBig.numShards());
        HollowSetTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int widthBig = dataElementsBig.bitsPerElement;
        long valBig = dataElementsBig.elementData.getElementValue(0, widthBig);

        assertTrue(widthBig > widthSmall);

        HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(new HollowSetTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        HollowSetTypeDataElements dataElementsJoined = joiner.join();
        int widthJoined = dataElementsJoined.bitsPerElement;

        long val0 = dataElementsJoined.elementData.getElementValue(0, widthJoined);
        long val1 = dataElementsJoined.elementData.getElementValue(dataElementsJoined.bitsPerSetPointer, widthJoined);

        assertEquals(widthBig, widthJoined);
        assertEquals(valSmall, val0);
        assertEquals(valBig, val1);
    }

//    @Test
//    public void testLopsidedShards() {
//      // TODO: implement when producer supports enabling type sharding for Set types
//    }
}
