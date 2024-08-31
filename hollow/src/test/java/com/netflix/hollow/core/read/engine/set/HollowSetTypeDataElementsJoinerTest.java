package com.netflix.hollow.core.read.engine.set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
                {0, 1, 2}
        };
        HollowSetTypeReadState typeReadState = populateTypeStateWith(setContents);
        assertEquals(1, typeReadState.numShards());

        setContents = new int[][] {
                {0, 1, 2},
                {3},
                {}
        };
        int setSize = 50;
        setContents[2] = new int[setSize];
        for (int i=4; i<setSize; i++) {
            setContents[2][i] = i;
        }
        HollowSetTypeReadState typeReadStateSharded = populateTypeStateWith(setContents);
        assertDataUnchanged(typeReadStateSharded, setContents);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(typeReadStateSharded.currentDataElements());
        HollowSetTypeDataElements joinedDataElements = joiner.join();

        HollowSetTypeReadStateShard joinedShard = new HollowSetTypeReadStateShard();
        joinedShard.setCurrentData(joinedDataElements);
        typeReadState = new HollowSetTypeReadState(MemoryMode.ON_HEAP, typeReadState.getSchema(), new HollowSetTypeReadStateShard[] {joinedShard});
        assertDataUnchanged(typeReadState, setContents);

        try {
            joiner = new HollowSetTypeDataElementsJoiner(mockSetTypeState.currentDataElements());
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

        int[] bigVals = new int[] {1000, 2000};
        HollowSetTypeReadState typeReadStateBig = populateTypeStateWith(new int[][] {bigVals});
        Set<Integer> setOfBigVals =  IntStream.of(bigVals).boxed().collect(Collectors.toSet());
        assertEquals(1, typeReadStateBig.numShards());
        HollowSetTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int widthBig = dataElementsBig.bitsPerElement;
        long bucketStart = 0;
        long valBig = dataElementsBig.elementData.getElementValue(bucketStart, widthBig);
        while (valBig == dataElementsBig.emptyBucketValue) {
            bucketStart += widthBig;
            valBig = dataElementsBig.elementData.getElementValue(bucketStart, widthBig);
        }

        HollowSetTypeDataElementsJoiner joiner = new HollowSetTypeDataElementsJoiner(new HollowSetTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        HollowSetTypeDataElements dataElementsJoined = joiner.join();
        int widthJoined = dataElementsJoined.bitsPerElement;

        long valSmallJoined = dataElementsJoined.elementData.getElementValue(0, widthJoined);
        bucketStart = dataElementsJoined.getStartBucket(1);
        long bucketEnd = dataElementsJoined.getEndBucket(1);
        Set<Integer> bigValsJoined = new HashSet<>();
        for (long bucket=bucketStart;bucket<bucketEnd;bucket++)  {
            int val = dataElementsJoined.getBucketValue(bucket);
            if (val != dataElementsJoined.emptyBucketValue) {
                bigValsJoined.add(val);
            }
        }

        assertTrue(widthBig > widthSmall);
        assertEquals(widthBig, widthJoined);
        assertEquals(valSmall, valSmallJoined);
        assertEquals(setOfBigVals, bigValsJoined);
    }

//    @Test
//    public void testLopsidedShards() {
//      // TODO: implement when producer supports enabling type sharding for Set types
//    }
}
