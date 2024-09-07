package com.netflix.hollow.core.read.engine;

import static com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy.shardingFactor;
import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeDataElements;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeShardsHolder;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import java.util.Random;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

public class HollowTypeReshardingStrategyTest {

    private HollowReadStateEngine readStateEngine;
    private HollowObjectSchema schema;

    @Test
    public void testMappingAnOrdinalToAShardAndBack() {
        int maxOrdinal = 1000;
        int numShards = 4;
        int minRecordLocationsPerShard = (maxOrdinal + 1) / numShards;
        int[][] shardOrdinals = new int[numShards][];
        for(int i=0;i<numShards;i++) {
            int maxShardOrdinal = (i < ((maxOrdinal + 1) & (numShards - 1))) ? minRecordLocationsPerShard : minRecordLocationsPerShard - 1;
            shardOrdinals[i] = new int[maxShardOrdinal + 1];
        }

        int shardNumberMask = numShards - 1;
        int shardOrdinalShift = 31 - Integer.numberOfLeadingZeros(numShards);

        for (int ordinal=0; ordinal<=maxOrdinal; ordinal++) {
            int shardIndex = ordinal & shardNumberMask;
            int shardOrdinal = ordinal >> shardOrdinalShift;
            shardOrdinals[shardIndex][shardOrdinal] = ordinal;
        }

        for (int shardIndex=0; shardIndex<numShards; shardIndex++) {
            for (int shardOrdinal=0; shardOrdinal<shardOrdinals[shardIndex].length; shardOrdinal++) {
                int ordinal = (shardOrdinal * numShards) + shardIndex;
                assertEquals(shardOrdinals[shardIndex][shardOrdinal], ordinal);
            }
        }
    }

    @Test
    public void testShardingFactor() {
        assertEquals(2, shardingFactor(1, 2));
        assertEquals(2, shardingFactor(2, 1));

        assertEquals(2, shardingFactor(4, 2));
        assertEquals(2, shardingFactor(2, 4));

        assertEquals(16, shardingFactor(1, 16));
        assertEquals(16, shardingFactor(32, 2));

        assertIllegalStateException(() -> shardingFactor(0, 1));
        assertIllegalStateException(() -> shardingFactor(2, 0));
        assertIllegalStateException(() -> shardingFactor(1, 1));
        assertIllegalStateException(() -> shardingFactor(1, -1));
        assertIllegalStateException(() -> shardingFactor(2, 3));
    }

    @Test
    public void testReshardingIntermediateStages_expandWithOriginalDataElements() throws Exception {
        for (int shardingFactor : new int[]{2, 4}) {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(5000))
            {
                HollowObjectTypeReadState expectedTypeState = populateTypeStateWith(numRecords);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(expectedTypeState);

                HollowObjectTypeShardsHolder original = expectedTypeState.getShardsVolatile();
                HollowObjectTypeReadState actualTypeState = new HollowObjectTypeReadState(readStateEngine, MemoryMode.ON_HEAP, schema, schema);
                actualTypeState.updateShardsVolatile(reshardingStrategy.expandWithOriginalDataElements(original, shardingFactor));

                assertEquals(shardingFactor * expectedTypeState.numShards(), actualTypeState.numShards());
                assertDataUnchanged(actualTypeState, numRecords);
            }
        }
    }

    @Test
    public void testReshardingIntermediateStages_splitDataElementsForOneShard() throws Exception {
        for (int shardingFactor : new int[]{2, 4}) {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(5000))
            {
                HollowObjectTypeReadState typeState = populateTypeStateWith(numRecords);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(typeState);

                HollowObjectTypeShardsHolder originalShardsHolder = typeState.getShardsVolatile();
                int originalNumShards = typeState.numShards();

                // expand shards
                typeState.updateShardsVolatile(reshardingStrategy.expandWithOriginalDataElements(originalShardsHolder, shardingFactor));

                for(int i=0; i<originalNumShards; i++) {
                    HollowTypeDataElements originalDataElements = typeState.getShardsVolatile().getShards()[i].getDataElements();

                    typeState.updateShardsVolatile(reshardingStrategy.splitDataElementsForOneShard(typeState, i, originalNumShards, shardingFactor));

                    assertEquals(shardingFactor * originalNumShards, typeState.numShards());
                    assertDataUnchanged(typeState, numRecords);   // as each original shard is processed

                    originalDataElements.destroy();
                }
            }
        }
    }

    @Test
    public void testReshardingIntermediateStages_joinDataElementsForOneShard() throws Exception {
        for (int shardingFactor : new int[]{2, 4, 8}) {
            for (int numRecords = 75000; numRecords <= 100000; numRecords += new Random().nextInt(1000)) {
                HollowObjectTypeReadState typeState = populateTypeStateWith(numRecords);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(typeState);

                HollowObjectTypeShardsHolder originalShardsHolder = typeState.getShardsVolatile();
                int originalNumShards = typeState.numShards();
                assertEquals(8, originalNumShards);

                int newNumShards = originalNumShards / shardingFactor;
                for (int i=0; i<newNumShards; i++) {
                    HollowTypeDataElements dataElementsToJoin[] = new HollowObjectTypeDataElements[shardingFactor];
                    for (int j=0; j<shardingFactor; j++) {
                        dataElementsToJoin[j] = originalShardsHolder.getShards()[i + (newNumShards*j)].getDataElements();
                    };

                    typeState.updateShardsVolatile(reshardingStrategy.joinDataElementsForOneShard(typeState, i, shardingFactor));

                    for (int j = 0; j < shardingFactor; j ++) {
                        dataElementsToJoin[j].destroy();
                    };

                    assertEquals(originalNumShards, typeState.numShards()); // numShards remains unchanged
                    assertDataUnchanged(typeState, numRecords);   // as each original shard is processed
                }
            }
        }
    }

    private HollowObjectTypeReadState populateTypeStateWith(int numRecords) throws IOException {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        schema = new HollowObjectSchema("TestObject", 4);
        schema.addField("longField", HollowObjectSchema.FieldType.LONG);
        schema.addField("stringField", HollowObjectSchema.FieldType.STRING);
        schema.addField("intField", HollowObjectSchema.FieldType.INT);
        schema.addField("doubleField", HollowObjectSchema.FieldType.DOUBLE);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
        writeStateEngine.setTargetMaxTypeShardSize(4 * 100 * 1024);

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for(int i=0;i<numRecords;i++) {
            rec.reset();
            rec.setLong("longField", i);
            rec.setString("stringField", "Value" + i);
            rec.setInt("intField", i);
            rec.setDouble("doubleField", i);

            writeStateEngine.add("TestObject", rec);
        }
        readStateEngine = new HollowReadStateEngine();
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine);
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    private void assertDataUnchanged(HollowObjectTypeReadState typeState, int numRecords) {
        for(int i=0;i<numRecords;i++) {
            GenericHollowObject obj = new GenericHollowObject(typeState, i);
            Assert.assertEquals(i, obj.getLong("longField"));
            Assert.assertEquals("Value"+i, obj.getString("stringField"));
            Assert.assertEquals((double)i, obj.getDouble("doubleField"), 0);
            if (typeState.getSchema().numFields() == 4) {   // filtered
                Assert.assertEquals(i, obj.getInt("intField"));
            }
        }
    }

    private void assertIllegalStateException(Supplier<Integer> invocation) {
        try {
            invocation.get();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

}
