package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState.shardingFactor;
import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import java.util.Random;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectTypeReadStateTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {
    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4 * 100 * 1024);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema));
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

    private void assertIllegalStateException(Supplier<Integer> invocation) {
        try {
            invocation.get();
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    // SNAP: TODO: test that after split/join, maxOrdinal for shard with no records should be -1 not 0

    @Test
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2, 4, 8, 16, 32, 64})   // , 128, 256, 512, 1024 // SNAP: TODO: OOM
        {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(1000))
            {
                HollowObjectTypeReadState objectTypeReadState = populateTypeStateWith(numRecords);
                assertDataUnchanged(numRecords);

                // Splitting shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    objectTypeReadState.reshard(newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(numRecords);

                // Joining shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    objectTypeReadState.reshard(newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(numRecords);
            }
        }
    }

    @Test
    public void testIntermediateStageOfResharding_expandWithOriginalDataElements() throws Exception {
        for (int shardingFactor : new int[]{2, 4, 8}) {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(1000))
            {
                HollowObjectTypeReadState expectedTypeState = populateTypeStateWith(numRecords);

                HollowObjectTypeReadState.ShardsHolder original = expectedTypeState.shardsVolatile;
                HollowObjectTypeReadState.ShardsHolder expanded = expectedTypeState.expandWithOriginalDataElements(original, shardingFactor);

                HollowObjectTypeReadState actualTypeState = new HollowObjectTypeReadState(readStateEngine, MemoryMode.ON_HEAP, schema, schema,
                        expanded.shards.length);
                actualTypeState.shardsVolatile = expanded;

                assertEquals(shardingFactor * expectedTypeState.numShards(), actualTypeState.numShards());
                assertDataUnchanged(actualTypeState, numRecords);
            }
        }
    }

    @Test
    public void testIntermediateStageOfResharding_splitDataElementsForOneShard() throws Exception {
        for (int shardingFactor : new int[]{2, 4}) {
            for(int numRecords=1;numRecords<=10000;numRecords+=new Random().nextInt(100))
            {
                HollowObjectTypeReadState expectedTypeState = populateTypeStateWith(numRecords);

                HollowObjectTypeReadState.ShardsHolder original = expectedTypeState.shardsVolatile;
                int originalNumShards = original.shards.length;

                // expansion step has to be called first to populate shards to the desired intermediate state
                HollowObjectTypeReadState.ShardsHolder expanded = expectedTypeState.expandWithOriginalDataElements(original, shardingFactor);

                for(int i=0; i<originalNumShards; i++) {
                    HollowObjectTypeDataElements originalDataElements = expanded.shards[i].currentDataElements();

                    HollowObjectTypeReadState.ShardsHolder afterSplit = expectedTypeState.splitDataElementsForOneShard(expanded,
                            i, originalNumShards, shardingFactor);
                    HollowObjectTypeReadState actualTypeState = new HollowObjectTypeReadState(readStateEngine, MemoryMode.ON_HEAP, schema, schema,
                            expanded.shards.length);
                    actualTypeState.shardsVolatile = afterSplit;

                    assertEquals(shardingFactor * expectedTypeState.numShards(), actualTypeState.numShards());
                    assertDataUnchanged(actualTypeState, numRecords);

                    originalDataElements.destroy();
                }
            }
        }
    }

    private void assertDataUnchanged(HollowObjectTypeReadState actualTypeState, int numRecords) {
        for(int i=0;i<numRecords;i++) {

            GenericHollowObject obj = new GenericHollowObject(actualTypeState , i);
            Assert.assertEquals(i, obj.getLong("longField"));
            Assert.assertEquals("Value"+i, obj.getString("stringField"));
            Assert.assertEquals(i, obj.getInt("intField"));
            Assert.assertEquals((double)i, obj.getDouble("doubleField"), 0);
        }
    }
}
