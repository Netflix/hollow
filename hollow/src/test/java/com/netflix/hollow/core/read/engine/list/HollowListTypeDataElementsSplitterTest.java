package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowListTypeDataElementsSplitterTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    @Test
    public void testSplit() throws IOException {
        HollowListTypeDataElementsSplitter splitter = new HollowListTypeDataElementsSplitter();

        int[][] listContents = new int[][] {
                {1, 2, 3},
                {2, 3},
                {0, 4}
        };
        HollowListTypeReadState typeReadState = populateTypeStateWith(5, listContents);
        assertEquals(1, typeReadState.numShards());
        assertDataUnchanged(typeReadState, listContents);

        HollowListTypeDataElements[] result1 = splitter.split(typeReadState.currentDataElements()[0], 1);
        typeReadState = new HollowListTypeReadState(typeReadState.getStateEngine(), typeReadState.getSchema(), 1);
        typeReadState.setCurrentData(result1[0]);
        assertDataUnchanged(typeReadState, listContents);

        HollowListTypeDataElements[] result8 = splitter.split(typeReadState.currentDataElements()[0], 8);
        assertEquals(0, result8[0].maxOrdinal);  // for index that landed one record after split
        assertEquals(-1, result8[7].maxOrdinal); // for index that landed no records after split

        try {
            splitter.split(typeReadState.currentDataElements()[0], 3);  // numSplits=3
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }

        try {
            splitter.split(typeReadState.currentDataElements()[0], 0);  // numSplits=0
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }
}
