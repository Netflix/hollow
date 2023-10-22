package com.netflix.hollow.core.read.engine.object;

import static com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState.shardingFactor;
import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
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
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2, 4, 8, 16})   // 32, 64, 128, 256, 512, 1024...
        {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(1000))
            {
                final int iterNumRecords = numRecords;
                HollowObjectTypeReadState objectTypeReadState = populateTypeStateWith(numRecords);
                CompletableFuture<Void> reads = CompletableFuture.runAsync(() -> {
                    for (int i=0; i<100; i++) {
                        assertDataUnchanged(objectTypeReadState, iterNumRecords);
                    }
                });

                // Splitting shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    objectTypeReadState.reshard(newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);

                // Joining shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    objectTypeReadState.reshard(newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);

                reads.get();
                if(reads.isCompletedExceptionally())
                    throw new IllegalStateException();
            }
        }
    }

    @Test
    public void testReshardingWithFilter() throws Exception {

        for (int shardingFactor : new int[]{2, 64})
        {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(10000))
            {
                HollowObjectTypeReadState objectTypeReadState = populateTypeStateWithFilter(numRecords);
                assertDataUnchanged(objectTypeReadState, numRecords);

                // Splitting shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    objectTypeReadState.reshard(newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);

                // Joining shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    objectTypeReadState.reshard(newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);
            }
        }
    }

    @Test
    public void testReshardingIntermediateStages_expandWithOriginalDataElements() throws Exception {
        for (int shardingFactor : new int[]{2, 4}) {
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(5000))
            {
                HollowObjectTypeReadState expectedTypeState = populateTypeStateWith(numRecords);

                HollowObjectTypeReadState.ShardsHolder original = expectedTypeState.shardsVolatile;
                HollowObjectTypeReadState.ShardsHolder expanded = expectedTypeState.expandWithOriginalDataElements(original, shardingFactor);

                HollowObjectTypeReadState actualTypeState = new HollowObjectTypeReadState(readStateEngine, MemoryMode.ON_HEAP, schema, schema);
                actualTypeState.shardsVolatile = expanded;

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

                HollowObjectTypeReadState.ShardsHolder originalShardsHolder = typeState.shardsVolatile;
                int originalNumShards = typeState.numShards();

                // expand shards
                typeState.shardsVolatile = typeState.expandWithOriginalDataElements(originalShardsHolder, shardingFactor);

                for(int i=0; i<originalNumShards; i++) {
                    HollowObjectTypeDataElements originalDataElements = typeState.shardsVolatile.shards[i].dataElements;

                    typeState.shardsVolatile = typeState.splitDataElementsForOneShard(typeState.shardsVolatile, i, originalNumShards, shardingFactor);

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

                HollowObjectTypeReadState.ShardsHolder originalShardsHolder = typeState.shardsVolatile;
                int originalNumShards = typeState.numShards();
                assertEquals(8, originalNumShards);

                int newNumShards = originalNumShards / shardingFactor;
                for (int i=0; i<newNumShards; i++) {
                    HollowObjectTypeDataElements dataElementsToJoin[] = new HollowObjectTypeDataElements[shardingFactor];
                    for (int j=0; j<shardingFactor; j++) {
                        dataElementsToJoin[j] = originalShardsHolder.shards[i + (newNumShards*j)].dataElements;
                    };

                    typeState.shardsVolatile = typeState.joinDataElementsForOneShard(typeState.shardsVolatile, i, shardingFactor);

                    for (int j = 0; j < shardingFactor; j ++) {
                        dataElementsToJoin[j].destroy();
                    };

                    assertEquals(originalNumShards, typeState.numShards()); // numShards remains unchanged
                    assertDataUnchanged(typeState, numRecords);   // as each original shard is processed
                }
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
