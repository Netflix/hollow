package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowListTypeDataElementsSplitterTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    @Test
    public void testSplit() throws IOException {

        int[][] listContents = new int[][] {
                {1, 2, 3},
                {2, 3},
                {0, 4}
        };
        HollowListTypeReadState typeReadState = populateTypeStateWith(listContents);
        assertEquals(1, typeReadState.numShards());
        assertDataUnchanged(typeReadState, listContents);

        HollowListTypeDataElementsSplitter splitter = new HollowListTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 1);
        HollowListTypeDataElements[] result1 = splitter.split();
        typeReadState = new HollowListTypeReadState(typeReadState.getStateEngine(), typeReadState.getSchema(), 1);
        typeReadState.setCurrentData(result1[0]);
        assertDataUnchanged(typeReadState, listContents);

        splitter = new HollowListTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 8);
        HollowListTypeDataElements[] result8 = splitter.split();
        assertEquals(0, result8[0].maxOrdinal);  // for index that landed one record after split
        assertEquals(-1, result8[7].maxOrdinal); // for index that landed no records after split

        try {
            splitter = new HollowListTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 3);
            splitter.split();  // numSplits=3
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }

        try {
            splitter = new HollowListTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 0);
            splitter.split();  // numSplits=0
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }
}
