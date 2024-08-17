package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectTypeDataElementsSplitterTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {

    @Test
    public void testSplit() throws IOException {
        HollowObjectTypeReadState typeReadState = populateTypeStateWith(5);
        assertEquals(1, typeReadState.numShards());
        assertDataUnchanged(typeReadState, 5);

        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 1);
        HollowObjectTypeDataElements[] result1 = (HollowObjectTypeDataElements[]) splitter.split();
        typeReadState = new HollowObjectTypeReadState(typeReadState.getSchema(), result1[0]);
        assertDataUnchanged(typeReadState, 5);

        splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 8);
        HollowObjectTypeDataElements[] result8 = (HollowObjectTypeDataElements[]) splitter.split();
        assertEquals(0, result8[0].maxOrdinal);  // for index that landed one record after split
        assertEquals(-1, result8[7].maxOrdinal); // for index that landed no records after split

        try {
            splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 3);
            splitter.split();  // numSplits=3
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }

        try {
            splitter = new HollowObjectTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 0);
            splitter.split();  // numSplits=0
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }
}
