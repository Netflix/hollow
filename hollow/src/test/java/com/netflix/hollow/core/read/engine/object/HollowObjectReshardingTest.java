package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import java.util.Random;
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
    public void testSplittingAndJoining() throws Exception {

        for (int shardingFactor : new int[]{2, 4, 8, 16, 32})
        // int shardingFactor = 2;
        {
            System.out.println("shardingFactor="+shardingFactor);
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(1000))
            // int numRecords = 3;
            {
                System.out.println("numRecords= " + numRecords);

                HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
                for(int i=0;i<numRecords;i++) {
                    rec.reset();
                    rec.setLong("longField", i);
                    rec.setInt("intField", i);
                    rec.setDouble("doubleField", i);
                    rec.setString("stringField", "Value" + i);

                    writeStateEngine.add("TestObject", rec);
                }
                roundTripSnapshot();
                assertDataUnchanged(numRecords);

                // Splitting shards
                {
                    HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    objectTypeReadState.reshard(newShardCount);

                    Assert.assertEquals(newShardCount, objectTypeReadState.numShards());
                    Assert.assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(numRecords);

                // joining shards
                {
                    HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    objectTypeReadState.reshard(newShardCount);

                    Assert.assertEquals(newShardCount, objectTypeReadState.numShards());
                    Assert.assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(numRecords);

                initWriteStateEngine();
            }
        }

        // TODO: test with holes
        // TODO: test with null values in fixed or variable length types
        // HollowExplorerUIServer s = new HollowExplorerUIServer(readStateEngine, 7001);
        // s.start();
        // s.join();
    }

    private void assertDataUnchanged(int numRecords) {
        for(int i=0;i<numRecords;i++) {
            GenericHollowObject obj = new GenericHollowObject(readStateEngine, "TestObject", i);

            try {
                Assert.assertEquals(i, obj.getLong("longField"));
            } catch (AssertionError e) {
                System.out.println("i="+ i);
            }
            Assert.assertEquals(i, obj.getInt("intField"));
            Assert.assertEquals((double)i, obj.getDouble("doubleField"), 0);
            Assert.assertEquals("Value"+i, obj.getString("stringField"));
        }
    }
}
