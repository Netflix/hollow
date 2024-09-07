package com.netflix.hollow.core.read.engine.set;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import java.util.Random;
import org.junit.Test;

public class HollowSetTypeReadStateTest extends AbstractHollowSetTypeDataElementsSplitJoinTest {

    @Test
    public void testResharding() throws Exception {

        for (int shardingFactor : new int[]{2, 4, 8, 16}) // , 32, 64, 128, 256, 512, 1024
        {
            for(int numRecords=1;numRecords<=1000;numRecords+=new Random().nextInt(100))
            {
                int[][] listContents = generateListContents(numRecords);
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
