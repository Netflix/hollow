package com.netflix.hollow.core.read.engine.object;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
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
