package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.read.engine.set.AbstractHollowSetTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeDataElements;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeDataElementsSplitter;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowMapTypeDataElementsSplitterTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {

    @Test
    public void testSplit() throws IOException {
        int[][][] maps = new int[][][] {
                { {1, 1}, {2, 2}, {3, 3} },
                { {1, 3}, {2, 1}, {3, 2} },
                { {1, 2} },
        };
        HollowMapTypeReadState typeReadState = populateTypeStateWith(maps);
        assertEquals(1, typeReadState.numShards());
        assertDataUnchanged(typeReadState, maps);

        HollowMapTypeDataElementsSplitter splitter = new HollowMapTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 1);
        HollowMapTypeDataElements[] result1 = splitter.split();
        typeReadState = new HollowMapTypeReadState(typeReadState.getSchema(), result1[0]);
        assertDataUnchanged(typeReadState, maps);

        splitter = new HollowMapTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 8);
        HollowMapTypeDataElements[] result8 = splitter.split();
        assertEquals(0, result8[0].maxOrdinal);  // for index that landed one record after split
        assertEquals(-1, result8[7].maxOrdinal); // for index that landed no records after split

        try {
            splitter = new HollowMapTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 3);
            splitter.split();  // numSplits=3
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }

        try {
            splitter = new HollowMapTypeDataElementsSplitter(typeReadState.currentDataElements()[0], 0);
            splitter.split();  // numSplits=0
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }
}
