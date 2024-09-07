package com.netflix.hollow.core.read.engine.list;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import java.util.Random;
import org.junit.Test;

public class HollowListTypeReadStateTest extends AbstractHollowListTypeDataElementsSplitJoinTest {

    @Test
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2, 4, 8, 16, 32, 64, 128, 256, 512, 1024})
        {
            for(int numRecords=1;numRecords<=1000;numRecords+=new Random().nextInt(100))
            {
                int[][] listContents = generateListContents(numRecords);
                HollowListTypeReadState listTypeReadState = populateTypeStateWith(listContents);
                assertDataUnchanged(listTypeReadState, listContents);
                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(listTypeReadState);

                // Splitting shards
                {
                    int prevShardCount = listTypeReadState.numShards();
                    int newShardCount = shardingFactor * prevShardCount;
                    reshardingStrategy.reshard(listTypeReadState, listTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, listTypeReadState.numShards());
                    assertEquals(newShardCount, shardingFactor * prevShardCount);
                }
                assertDataUnchanged(listTypeReadState, listContents);

                // Joining shards
                {
                    int prevShardCount = listTypeReadState.numShards();
                    int newShardCount = prevShardCount / shardingFactor;
                    reshardingStrategy.reshard(listTypeReadState, listTypeReadState.numShards(), newShardCount);

                    assertEquals(newShardCount, listTypeReadState.numShards());
                    assertEquals(shardingFactor * newShardCount, prevShardCount);
                }
                assertDataUnchanged(listTypeReadState, listContents);
            }
        }
    }
}
