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

        for (int shardingFactor : new int[]{2, 4, 8, 16, 32}) {
            System.out.println("shardingFactor="+shardingFactor);
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(300)) {
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
                    HollowObjectTypeReadState.ShardsHolder newShardsHolder = new HollowObjectTypeReadState.ShardsHolder(newShardCount);

                    for (int i = 0; i < prevShardCount; i++) {
                        HollowObjectTypeReadState.ShardsHolder shardsHolder = objectTypeReadState.shardsVolatile;
                        HollowObjectTypeDataElements preSplitDataElements = shardsHolder.shards[i].currentDataElements();
                        int finalShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newShardCount);

                        HollowObjectTypeDataElementsSplitter splitter = new HollowObjectTypeDataElementsSplitter(preSplitDataElements, shardingFactor);
                        HollowObjectTypeDataElements[] splits = splitter.split();

                        for (int j = 0; j < shardingFactor; j ++) {
                            IHollowObjectTypeReadStateShard finalShard = new HollowObjectTypeReadStateShard(schema, finalShardOrdinalShift, 0);
                            finalShard.setCurrentData(newShardsHolder, splits[j]);
                            newShardsHolder.shards[i + (prevShardCount*j)] = finalShard;
                        }

                        preSplitDataElements.destroySpecial();
                    }
                    objectTypeReadState.shardsVolatile = newShardsHolder;
                    Assert.assertEquals(newShardCount, objectTypeReadState.numShards());
                    Assert.assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(numRecords);

                // joining shards
                {
                    HollowObjectTypeReadState objectTypeReadState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    HollowObjectTypeReadState.ShardsHolder newShardsHolder = new HollowObjectTypeReadState.ShardsHolder(newShardCount);

                    for (int i = 0; i < newShardCount; i++) {
                        HollowObjectTypeReadState.ShardsHolder shardsHolder = objectTypeReadState.shardsVolatile;
                        HollowObjectTypeDataElements[] preJoinDatElements = new HollowObjectTypeDataElements[shardingFactor];
                        for (int j = 0; j < shardingFactor; j ++) {
                            preJoinDatElements[j] = shardsHolder.shards[i + (newShardCount*j)].currentDataElements();
                        };
                        int finalShardOrdinalShift = 31 - Integer.numberOfLeadingZeros(newShardCount);

                        HollowObjectTypeDataElementsJoiner joiner = new HollowObjectTypeDataElementsJoiner(preJoinDatElements);
                        HollowObjectTypeDataElements joined = joiner.join();

                        IHollowObjectTypeReadStateShard finalShard = new HollowObjectTypeReadStateShard(schema, finalShardOrdinalShift, 0);
                        finalShard.setCurrentData(newShardsHolder, joined);
                        newShardsHolder.shards[i] = finalShard;

                        for (int j = 0; j < shardingFactor; j ++) {
                            preJoinDatElements[j].destroySpecial();
                        };
                    }
                    objectTypeReadState.shardsVolatile = newShardsHolder;
                    Assert.assertEquals(newShardCount, objectTypeReadState.numShards());
                    Assert.assertEquals(shardingFactor * newShardCount, prevShardCount);

                }
                assertDataUnchanged(numRecords);
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
