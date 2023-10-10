package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
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
                .build();

        p.initializeDataModel(schema);
        p.getWriteEngine().getTypeState("TestObject").setNumShards(2);
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

        long v2 = oneRunCycle(p, new int[] {0, 1, 2, 3});
        c.triggerRefreshTo(v2);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        long v3 = oneRunCycle(p, new int[] { 0, 1});     // drop to 1 ordinal per shard, skipTypeShardWithNoAdds will make it so that maxOrdinal is adjusted
        c.triggerRefreshTo(v3);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        long v4 = oneRunCycle(p, new int[] { 0, 1, 2}); // now add another ordinal to one shard, maxOrdinals will be lopsided
        c.triggerRefreshTo(v4);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());

        HollowObjectTypeReadState movieTypeState = (HollowObjectTypeReadState) c.getStateEngine().getTypeState("TestObject");
        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner();
        HollowObjectTypeDataElements joined = joiner.join(new HollowObjectTypeDataElements[]
                {movieTypeState.shardsVolatile.shards[0].currentDataElements(),
                        movieTypeState.shardsVolatile.shards[1].currentDataElements()});

        assertEquals(c.getStateEngine().getTypeState("TestObject").maxOrdinal(), joined.maxOrdinal);
        assertEquals(2, c.getStateEngine().getTypeState("TestObject").numShards());
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

    private void populate(HollowWriteStateEngine wse, HollowReadStateEngine rse, int[] recordIds) throws IOException {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for(int recordId : recordIds) {
            rec.reset();
            rec.setLong("longField", recordId);
            rec.setString("stringField", "Value" + recordId);
            rec.setInt("intField", recordId);
            rec.setDouble("doubleField", recordId);

            wse.add("TestObject", rec);
        }
        StateEngineRoundTripper.roundTripSnapshot(wse, rse);
    }


}
