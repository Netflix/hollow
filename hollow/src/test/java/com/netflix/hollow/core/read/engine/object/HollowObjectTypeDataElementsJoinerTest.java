package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.test.InMemoryBlobStore;
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
        assertDataUnchanged(typeReadStateSharded, 5);
        assertEquals(8, typeReadStateSharded.numShards());

        HollowObjectTypeDataElements joinedDataElements = joiner.join(typeReadStateSharded.currentDataElements());

        typeReadState = new HollowObjectTypeReadState(typeReadState.getSchema(), joinedDataElements);
        assertDataUnchanged(typeReadState, 5);

        try {
            joiner.join(mockObjectTypeState.currentDataElements());
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected, numSplits should be a power of 2
        }
    }

    @Test
    public void testJoinDifferentFieldWidths() throws IOException {
        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();

        HollowObjectTypeReadState typeReadStateSmall = populateTypeStateWith(new int[] {1});
        assertEquals(1, typeReadStateSmall.numShards());
        HollowObjectTypeDataElements dataElementsSmall = typeReadStateSmall.currentDataElements()[0];
        int intFieldPosSmall = dataElementsSmall.schema.getPosition("intField");
        int widthSmall = dataElementsSmall.bitsPerField[intFieldPosSmall];
        long valSmall = dataElementsSmall.fixedLengthData.getElementValue(dataElementsSmall.bitOffsetPerField[intFieldPosSmall], widthSmall);

        HollowObjectTypeReadState typeReadStateBig = populateTypeStateWith(new int[] {2});
        assertEquals(1, typeReadStateBig.numShards());
        HollowObjectTypeDataElements dataElementsBig = typeReadStateBig.currentDataElements()[0];
        int intFieldPosBig = dataElementsBig.schema.getPosition("intField");
        int widthBig = dataElementsBig.bitsPerField[intFieldPosBig];
        long valBig = dataElementsBig.fixedLengthData.getElementValue(dataElementsBig.bitOffsetPerField[intFieldPosBig], widthBig);

        assertTrue(widthBig > widthSmall);

        HollowObjectTypeDataElements dataElementsJoined = joiner.join(new HollowObjectTypeDataElements[]
                {dataElementsSmall, dataElementsBig});
        int intFieldPosJoined = dataElementsJoined.schema.getPosition("intField");
        int widthJoined = dataElementsJoined.bitsPerField[intFieldPosJoined];

        long val0 = dataElementsJoined.fixedLengthData.getElementValue(dataElementsJoined.bitOffsetPerField[intFieldPosJoined], widthJoined);
        long val1 = dataElementsJoined.fixedLengthData.getElementValue(dataElementsJoined.bitsPerRecord + dataElementsJoined.bitOffsetPerField[intFieldPosJoined], widthJoined);

        assertEquals(widthBig, widthJoined);
        assertEquals(valSmall, val0);
        assertEquals(valBig, val1);
    }

    @Test
    public void testLopsidedShards() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer p = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withTypeResharding()
                .build();

        p.initializeDataModel(schema);
        int targetSize = 64;
        p.getWriteEngine().setTargetMaxTypeShardSize(targetSize);
        long v1 = oneRunCycle(p, new int[] {0, 1, 2, 3, 4, 5, 6, 7});

        HollowConsumer c = HollowConsumer
                .withBlobRetriever(blobStore)
                .withDoubleSnapshotConfig(new HollowConsumer.DoubleSnapshotConfig() {
                    @Override
                    public boolean allowDoubleSnapshot() {
                        return false;
                    }

                    @Override
                    public int maxDeltasBeforeDoubleSnapshot() {
                        return Integer.MAX_VALUE;
                    }
                })
                .withSkipTypeShardUpdateWithNoAdditions()
                .build();
        c.triggerRefreshTo(v1);

        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());
        assertEquals(true, c.getStateEngine().isSkipTypeShardUpdateWithNoAdditions());

        long v2 = oneRunCycle(p, new int[] {0, 1, 2, 3, 5, 7});
        c.triggerRefreshTo(v2);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        long v3 = oneRunCycle(p, new int[] { 0, 1, 3, 5}); // drop to 1 ordinal per shard, skipTypeShardWithNoAdds will make it so that maxOrdinal is adjusted
        c.triggerRefreshTo(v3);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        long v4 = oneRunCycle(p, new int[] { 0, 1, 2, 3}); // now add another ordinal to one shard, maxOrdinals will be lopsided
        c.triggerRefreshTo(v4);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        readStateEngine = c.getStateEngine();
        assertDataUnchanged(3);

        long v5 = oneRunCycle(p, new int[] {0, 1});

        // assert lopsided shards before join
        assertEquals(2, ((HollowObjectTypeReadState) c.getStateEngine().getTypeState("TestObject")).shardsVolatile.shards[0].dataElements.maxOrdinal);
        assertEquals(3, ((HollowObjectTypeReadState) c.getStateEngine().getTypeState("TestObject")).shardsVolatile.shards[1].dataElements.maxOrdinal);
        c.triggerRefreshTo(v5);
        assertEquals(1, c.getStateEngine().getTypeState("TestObject").numShards()); // joined to 1 shard
        readStateEngine = c.getStateEngine();
        assertDataUnchanged(2);

        long v6 = oneRunCycle(p, new int[] {0, 1, 2, 3, 4, 5 });
        c.triggerRefreshTo(v6);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards()); // split to 2 shards

        long v7 = oneRunCycle(p, new int[] {8, 9});
        c.triggerRefreshTo(v7);
        assertEquals(4, c.getStateEngine().getTypeState("TestObject").numShards()); // still 2 shards

        long v8 = oneRunCycle(p, new int[] {8});
        c.triggerRefreshTo(v8);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards()); // down to 1 shard

        c.triggerRefreshTo(v1);
        assertEquals(v1, c.getCurrentVersionId());

        c.triggerRefreshTo(v8);
        assertEquals(v8, c.getCurrentVersionId());
    }

    private long oneRunCycle(HollowProducer p, int recordIds[]) {
        return p.runCycle(state -> {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            for(int recordId : recordIds) {
                rec.reset();
                rec.setLong("longField", recordId);
                rec.setString("stringField", "Value" + recordId);
                rec.setInt("intField", recordId);
                rec.setDouble("doubleField", recordId);

                state.getStateEngine().add("TestObject", rec);
            }
        });
    }
}
