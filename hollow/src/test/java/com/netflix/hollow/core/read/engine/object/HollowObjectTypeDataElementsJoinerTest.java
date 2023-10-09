package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectTypeDataElementsJoinerTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(16);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

    @Test
    public void testJoin() throws IOException {
        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();

        HollowObjectTypeReadState typeReadState = populateTypeStateWith(1);
        assertEquals(1, typeReadState.numShards());

        HollowObjectTypeReadState typeReadStateSharded = populateTypeStateWith(5);
        assertDataUnchanged(5);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowObjectTypeDataElements joinedDataElements = joiner.join(typeReadStateSharded.currentDataElements());

        typeReadState.setCurrentData(joinedDataElements);
        assertDataUnchanged(5);

        try {
            joiner.join(mockObjectTypeState.currentDataElements());
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }
}
