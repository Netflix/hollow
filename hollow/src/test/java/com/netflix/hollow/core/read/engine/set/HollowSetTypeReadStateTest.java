package com.netflix.hollow.core.read.engine.set;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.Random;
import org.junit.Test;

public class HollowSetTypeReadStateTest extends AbstractHollowSetTypeDataElementsSplitJoinTest {

    @Test
    public void testApproximateHoleCostWithShards() throws Exception {
        // Setup with 2 shards
        writeStateEngine = new HollowWriteStateEngine();
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema, 2));
        writeStateEngine.addTypeState(new HollowSetTypeWriteState(setSchema, 2));

        // Cycle 1:
        // populate TestObject with ordinal values 0 - 5
        populateWriteStateEngine(writeStateEngine, schema, 6);
        // populate TestSet with ordinal values 0 - 5
        populateTypeStateWith(writeStateEngine, new int[][] {{0}, {1}, {2}, {3}, {4}, {5}});
        roundTripSnapshot();

        // Cycle 2: re-add all TestObjects
        // for TestSet:
        // Shard 0 (even: 0=hole, 2=pop, 4=pop) -> 1 hole
        // Shard 1 (odd: 1=hole, 3=hole, 5=hole) -> 3 holes
        populateWriteStateEngine(writeStateEngine, schema, 6);
        // populate TestSet with ordinal values 0 - 4
        populateTypeStateWith(writeStateEngine, new int[][] {{2}, {4}});
        roundTripDelta();

        HollowSetTypeReadState typeState = (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");
        assertEquals(2, typeState.numShards());
        assertEquals(2, typeState.getPopulatedOrdinals().cardinality());
        HollowSetTypeReadStateShard[] shards = (HollowSetTypeReadStateShard[])typeState.getShardsVolatile().getShards();
        long holeCost = (shards[0].dataElements.bitsPerFixedLengthSetPortion +
                shards[1].dataElements.bitsPerFixedLengthSetPortion*3L) >>> 3;
        assertEquals(holeCost, typeState.getApproximateHoleCostInBytes());
    }

    @Test
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2}) { // , 4, 8, 16, 32, 64, 128, 256, 512, 1024
            for(int numRecords=1;numRecords<=1000;numRecords+=new Random().nextInt(300)) {
                int[][] listContents = generateSetContents(numRecords);
                HollowSetTypeReadState setTypeReadState = populateTypeStateWith(listContents);
                assertDataUnchanged(setTypeReadState, listContents);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(setTypeReadState);

                // Splitting shards
                {
                    int prevShardCount = setTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    reshardingStrategy.reshard(setTypeReadState, setTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, setTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(setTypeReadState, listContents);

                // Joining shards
                {
                    int prevShardCount = setTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    reshardingStrategy.reshard(setTypeReadState, setTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, setTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(setTypeReadState, listContents);
            }
        }
    }
}
