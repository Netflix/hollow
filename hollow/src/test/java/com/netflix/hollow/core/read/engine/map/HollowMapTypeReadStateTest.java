package com.netflix.hollow.core.read.engine.map;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.Random;
import org.junit.Test;

public class HollowMapTypeReadStateTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {

    @Test
    public void testApproximateHoleCostWithShards() throws Exception {
        // Setup with 2 shards
        writeStateEngine = new HollowWriteStateEngine();
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(schema, 2));
        writeStateEngine.addTypeState(new HollowMapTypeWriteState(mapSchema, 2));

        // Cycle 1: add TestObject records and 6 map records with varying sizes
        // Cycle 1
        // populate TestObject with ordinal values 0 - 5
        populateWriteStateEngine(writeStateEngine, schema, 6);
        // populate TestMap with map entries, each containing ordinal value {x : x}, where x is from 0-5
        populateWriteStateEngineWithMap(writeStateEngine, new int[][][] {{{0,0}}, {{1,1}}, {{2,2}}, {{3,3}}, {{4,4}}, {{5,5}}});
        roundTripSnapshot();

        // Cycle 2: re-add all TestObjects
        // for TestMap:
        // Shard 0 (even: 0=hole, 2=pop, 4=pop) -> 1 hole
        // Shard 1 (odd: 1=hole, 3=hole, 5=hole) -> 3 holes
        populateWriteStateEngine(writeStateEngine, schema, 6);
        // populate TestMap with ordinal values 2 and 4
        populateWriteStateEngineWithMap(writeStateEngine, new int[][][] {{{2,2}}, {{4,4}}});
        roundTripDelta();

        HollowMapTypeReadState typeState = (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");
        assertEquals(2, typeState.numShards());
        assertEquals(2, typeState.getPopulatedOrdinals().cardinality());
        HollowMapTypeReadStateShard[] shards = (HollowMapTypeReadStateShard[])typeState.getShardsVolatile().getShards();
        long holeCost = shards[0].dataElements.bitsPerFixedLengthMapPortion/8L +
                shards[1].dataElements.bitsPerFixedLengthMapPortion*3L/8;
        assertEquals(holeCost, typeState.getApproximateHoleCostInBytes());
    }

    @Test
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2}) { // , 4, 8, 16, 32, 64, 128, 256, 512, 1024
            for(int numRecords=1;numRecords<=1000;numRecords+=new Random().nextInt(100)) {
                int[][][] listContents = generateMapContents(numRecords);
                HollowMapTypeReadState mapTypeReadState = populateTypeStateWith(listContents);
                assertDataUnchanged(mapTypeReadState, listContents);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(mapTypeReadState);

                // Splitting shards
                {
                    int prevShardCount = mapTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    reshardingStrategy.reshard(mapTypeReadState, mapTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, mapTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(mapTypeReadState, listContents);

                // Joining shards
                {
                    int prevShardCount = mapTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    reshardingStrategy.reshard(mapTypeReadState, mapTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, mapTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(mapTypeReadState, listContents);
            }
        }
    }
}
