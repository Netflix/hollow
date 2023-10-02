package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.util.Random;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowObjectTypeReadStateTest extends AbstractStateEngineTest {
    HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("TestObject", 4);
        schema.addField("longField", HollowObjectSchema.FieldType.LONG);
        schema.addField("intField", HollowObjectSchema.FieldType.INT);
        schema.addField("doubleField", HollowObjectSchema.FieldType.DOUBLE);
        schema.addField("stringField", HollowObjectSchema.FieldType.STRING);

        super.setUp();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
    }

//    @Test
//    public void testSimpleSplitAndJoin() throws Exception {
//        int shardingFactor = 2;
//        {
//            System.out.println("shardingFactor="+shardingFactor);
//            int numRecords = 1;
//            {
//                System.out.println("numRecords= " + numRecords);
//
//                HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
//                for(int i=0;i<numRecords;i++) {
//                    rec.reset();
//                    rec.setLong("longField", i);
//                    rec.setInt("intField", i);
//                    rec.setDouble("doubleField", i);
//                    rec.setString("stringField", "Value" + i);
//
//                    writeStateEngine.add("TestObject", rec);
//                }
//                roundTripSnapshot();
//                assertDataUnchanged(numRecords);
//
//                // Splitting shards
//                {
//                    HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
//                    int prevShardCount = objectTypeReadState.numShards();
//                    int newShardCount = shardingFactor * prevShardCount;
//                    objectTypeReadState.reshard(newShardCount);
//
//                    Assert.assertEquals(newShardCount, objectTypeReadState.numShards());
//                    Assert.assertEquals(newShardCount, shardingFactor * prevShardCount);
//                }
//                assertDataUnchanged(numRecords);
//
//                // joining shards
//                {
//                    HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
//                    int prevShardCount = objectTypeReadState.numShards();
//                    int newShardCount = prevShardCount / shardingFactor;
//                    objectTypeReadState.reshard(newShardCount);
//
//                    Assert.assertEquals(newShardCount, objectTypeReadState.numShards());
//                    Assert.assertEquals(shardingFactor * newShardCount, prevShardCount);
//                }
//                assertDataUnchanged(numRecords);
//
//                initWriteStateEngine();
//            }
//        }
//    }

    // SNAP: TODO: test that after split/join, maxOrdinal for shard with no records should be -1 not 0

    @Test
    public void testSplittingAndJoining() throws Exception {

        for (int shardingFactor : new int[]{2, 4, 8, 16, 32, 64})   // , 128, 256, 512, 1024 // SNAP: TODO: OOM
        {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(1000))
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
