package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectReshardingTest extends AbstractStateEngineTest {
    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 3);
        schema.addField("longField", HollowObjectSchema.FieldType.LONG);
        schema.addField("intField", HollowObjectSchema.FieldType.INT);
        schema.addField("doubleField", HollowObjectSchema.FieldType.DOUBLE);

        super.setUp();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

    @Test
    public void testSplittingAndJoining() throws IOException {

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);

        for(int i=0;i<1000;i++) {
            rec.reset();
            rec.setLong("longField", i);
            rec.setInt("intField", i);
            rec.setDouble("doubleField", i);

            writeStateEngine.add("TestObject", rec);
        }

        roundTripSnapshot();
        Assert.assertEquals(4, readStateEngine.getTypeState("TestObject").numShards());

        for(int i=0;i<1000;i++) {
            GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TestObject", i);

            Assert.assertEquals(i, obj.getLong("longField"));
            Assert.assertEquals(i, obj.getInt("intField"));
            Assert.assertEquals((double)i, obj.getDouble("doubleField"), 0);
        }

        HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
        int currShardCount = objectTypeReadState.numShards();
        int newShardCount = 2 * currShardCount;
        HollowObjectTypeReadState.ShardsHolder newShardsHolder = new HollowObjectTypeReadState.ShardsHolder(newShardCount);

        for(int i=0;i<currShardCount;i++) {
            HollowObjectTypeReadState.ShardsHolder shardsHolder = objectTypeReadState.shardsVolatile;
            HollowObjectTypeDataElements preSplitDataElements = shardsHolder.shards[i].currentDataElements();
            int finalShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newShardCount);

            HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(preSplitDataElements, 2);
            HollowObjectTypeDataElements[] splits = splitter.split();

            IHollowObjectTypeReadStateShard finalShardLeft = new HollowObjectTypeReadStateShard((HollowObjectSchema) schema, finalShardOrdinalShift, 0);
            finalShardLeft.setCurrentData(newShardsHolder, splits[0]);
            newShardsHolder.shards[i] = finalShardLeft;

            IHollowObjectTypeReadStateShard finalShardRight = new HollowObjectTypeReadStateShard((HollowObjectSchema) schema, finalShardOrdinalShift, 0);
            finalShardRight.setCurrentData(newShardsHolder, splits[1]);
            newShardsHolder.shards[i + currShardCount] = finalShardRight;

            preSplitDataElements.destroy();
        }

        objectTypeReadState.shardsVolatile = newShardsHolder;

        Assert.assertEquals(newShardCount, objectTypeReadState.numShards());
        // can still read sae data
        for(int i=0;i<1000;i++) {
            GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TestObject", i);

            Assert.assertEquals(i, obj.getLong("longField"));
            Assert.assertEquals(i, obj.getInt("intField"));
            Assert.assertEquals((double)i, obj.getDouble("doubleField"), 0);
        }
    }
}
