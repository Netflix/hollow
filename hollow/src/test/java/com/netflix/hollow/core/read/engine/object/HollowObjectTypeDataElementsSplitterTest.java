package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectTypeDataElementsSplitterTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {

    @Test
    public void testSplit() throws IOException {
        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter();

        HollowObjectTypeReadState typeReadState = populateTypeStateWith(5);
        assertEquals(1, typeReadState.numShards());
        assertDataUnchanged(5);

        HollowObjectTypeDataElements[] result1 = splitter.split(typeReadState.currentDataElements()[0], 1);
        typeReadState = new HollowObjectTypeReadState(typeReadState.getSchema(), result1[0]);
        // typeReadState.setCurrentData(result1[0]);   // SNAP: TODO: Remove
        assertDataUnchanged(5);

        HollowObjectTypeDataElements[] result8 = splitter.split(typeReadState.currentDataElements()[0], 8);
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
