package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectTypeDataElementsJoinerTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {
    @Test
    public void testJoin() throws IOException {
        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();

        HollowObjectTypeReadState typeReadState = populateTypeStateWith(5);
        assertEquals(1, typeReadState.numShards());
        assertDataUnchanged(5);

        HollowObjectTypeDataElements joinedDataElements = joiner.join(typeReadState.currentDataElements());
        typeReadState.setCurrentData(joinedDataElements);
        assertDataUnchanged(5);

        try {
            joiner.join(mockObjectTypeState.currentDataElements());
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        } catch (Exception e) {
            Assert.fail();
        }
    }
}
