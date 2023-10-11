package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Assert;
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

    @Test
    public void testSplitThenJoinWithFilter() throws IOException {
        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter();
        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();

        int numSplits = 2;
        for (int numRecords=0;numRecords<1*1000;numRecords++) {
            HollowObjectTypeReadState typeReadState = populateTypeStateWithFilter(numRecords);
            assertEquals(1, typeReadState.numShards());
            assertDataUnchanged(numRecords);
            HollowChecksum origChecksum = typeReadState.getChecksum(typeReadState.getSchema());

            HollowObjectTypeDataElements[] splitElements = splitter.split(typeReadState.currentDataElements()[0], numSplits);
            HollowObjectTypeDataElements joinedElements = joiner.join(splitElements);
            typeReadState.setCurrentData(joinedElements);

            assertDataUnchanged(numRecords);
            HollowChecksum resultChecksum = typeReadState.getChecksum(typeReadState.getSchema());
            assertEquals(origChecksum, resultChecksum);
        }
    }

    @Test
    public void testSplitThenJoinWithEmptyJoin() throws IOException {
        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter();

        HollowObjectTypeReadState typeReadState = populateTypeStateWith(1);
        assertEquals(1, typeReadState.numShards());

        HollowObjectTypeDataElements[] splitBy4 = splitter.split(typeReadState.currentDataElements()[0], 4);
        assertEquals(-1, splitBy4[1].maxOrdinal);
        assertEquals(-1, splitBy4[3].maxOrdinal);

        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();
        HollowObjectTypeDataElements joined = joiner.join(new HollowObjectTypeDataElements[]{splitBy4[1], splitBy4[3]});

        assertEquals(-1, joined.maxOrdinal);
    }

    // manually invoked
    // @Test
    public void testSplittingAndJoiningWithSnapshotBlob() throws Exception {

        String blobPath = null; // dir where snapshot blob exists for e.g. "/tmp/";
        long v = 0l; // snapshot version for e.g. 20230915162636001l;
        String objectTypeWithOneShard = null; // type name corresponding to an Object type with single shard for e.g. "Movie";
        int numSplits = 2;

        if (blobPath==null || v==0l || objectTypeWithOneShard==null) {
            throw new IllegalArgumentException("These arguments need to be specified");
        }
        HollowFilesystemBlobRetriever hollowBlobRetriever = new HollowFilesystemBlobRetriever(Paths.get(blobPath));
        HollowConsumer c = HollowConsumer.withBlobRetriever(hollowBlobRetriever).build();
        c.triggerRefreshTo(v);
        HollowReadStateEngine readStateEngine = c.getStateEngine();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState(objectTypeWithOneShard);
        HollowSchema origSchema = typeState.getSchema();
        HollowChecksum originalChecksum = typeState.getChecksum(origSchema);

        assertEquals(1, typeState.numShards());

        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter();
        HollowObjectTypeDataElements[] splitElements = splitter.split(typeState.currentDataElements()[0], numSplits);

        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();
        HollowObjectTypeDataElements joinedElements = joiner.join(splitElements);

        typeState.setCurrentData(joinedElements);
        HollowChecksum newChecksum = typeState.getChecksum(origSchema);

        Assert.assertEquals(originalChecksum, newChecksum);
    }
}
