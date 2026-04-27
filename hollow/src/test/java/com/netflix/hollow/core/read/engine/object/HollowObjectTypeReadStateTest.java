package com.netflix.hollow.core.read.engine.object;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.Random;
import org.junit.Test;

public class HollowObjectTypeReadStateTest extends AbstractHollowObjectTypeDataElementsSplitJoinTest {
    @Test
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2}) { // , 4, 8, 16, 32, 64, 128, 256, 512, 1024
            for(int numRecords=1;numRecords<=10000;numRecords+=new Random().nextInt(1000))
            {
                HollowObjectTypeReadState objectTypeReadState = populateTypeStateWith(numRecords);
                assertDataUnchanged(objectTypeReadState, numRecords);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(objectTypeReadState);

                // Splitting shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    reshardingStrategy.reshard(objectTypeReadState, objectTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);

                // Joining shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    reshardingStrategy.reshard(objectTypeReadState, objectTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);
            }
        }
    }

    @Test
    public void testApproximateHoleCostWithShards() throws Exception {
        // Setup with 2 shards
        writeStateEngine = new HollowWriteStateEngine();
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema, 2));

        // Cycle 1: snapshot with 6 records (ordinals 0-5)
        // note that the ordinal assigned would be also be 0-4.
        populateWriteStateEngine(writeStateEngine, schema, 6);
        roundTripSnapshot();
        // Cycle 2: keep only records 2 and 4, creating holes at 0, 1, 3, 5
        populateWriteStateEngine(writeStateEngine, schema, new int[]{2, 4});
        roundTripDelta();

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
        assertEquals(2, typeState.numShards());
        assertEquals(2, typeState.getPopulatedOrdinals().cardinality());

        // Shard 0 (even: 0=hole, 2=pop, 4=pop) -> 1 hole
        // Shard 1 (odd: 1=hole, 3=hole, 5=hole) -> 3 holes
        HollowObjectTypeReadStateShard[] shards = (HollowObjectTypeReadStateShard[])typeState.getShardsVolatile().getShards();
        long holeCost = (shards[0].dataElements.bitsPerRecord + shards[1].dataElements.bitsPerRecord*3L) >>> 3;
        assertEquals(holeCost, typeState.getApproximateHoleCostInBytes());
    }

    @Test
    public void testReshardingWithFilter() throws Exception {
        for (int shardingFactor : new int[]{2}) { // , 4, 8, 16, 32, 64, 128, 256, 512, 1024
            for(int numRecords=1;numRecords<=100000;numRecords+=new Random().nextInt(10000)) {
                HollowObjectTypeReadState objectTypeReadState = populateTypeStateWithFilter(numRecords);
                assertDataUnchanged(objectTypeReadState, numRecords);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(objectTypeReadState);

                // Splitting shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    reshardingStrategy.reshard(objectTypeReadState, objectTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);

                // Joining shards
                {
                    int prevShardCount = objectTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    reshardingStrategy.reshard(objectTypeReadState, objectTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, objectTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(objectTypeReadState, numRecords);
            }
        }
    }
}
