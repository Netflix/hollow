package com.netflix.hollow.core.read.engine.map;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import java.util.Random;
import org.junit.Test;

public class HollowMapTypeReadStateTest extends AbstractHollowMapTypeDataElementsSplitJoinTest {

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
