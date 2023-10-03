package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import org.junit.Test;

public class HollowObjectTypeDataElementsSplitJoinTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4 * 1000 * 1024);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

    @Test
    public void testSplitThenJoin() throws IOException {
        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter();
        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();

        for (int numRecords=0;numRecords<1*1000;numRecords++) {
            HollowObjectTypeReadState typeReadState = populateTypeStateWith(numRecords);
            assertEquals(1, typeReadState.numShards());
            assertDataUnchanged(numRecords);
            HollowChecksum origChecksum = typeReadState.getChecksum(typeReadState.getSchema());

            for (int numSplits : new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}) {
                HollowObjectTypeDataElements[] splitElements = splitter.split(typeReadState.currentDataElements()[0], numSplits);
                HollowObjectTypeDataElements joinedElements = joiner.join(splitElements);
                typeReadState.setCurrentData(joinedElements);

                assertDataUnchanged(numRecords);
                HollowChecksum resultChecksum = typeReadState.getChecksum(typeReadState.getSchema());
                assertEquals(origChecksum, resultChecksum);
            }
        }
    }
}
