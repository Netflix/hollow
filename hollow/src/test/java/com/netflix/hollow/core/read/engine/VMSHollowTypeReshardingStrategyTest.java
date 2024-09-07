package com.netflix.hollow.core.read.engine;

import static junit.framework.TestCase.assertEquals;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.fs.HollowFilesystemBlobRetriever;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import java.nio.file.Paths;
import org.junit.Test;

public class VMSHollowTypeReshardingStrategyTest {

    @Test
    public void testSplittingAndJoiningWithSnapshotBlob() throws Exception {

        String blobPath = "/Users/ssingh/workspace/blob-cache/vms-daintree/prod/"; // null; // dir where snapshot blob exists for e.g. "/tmp/";
        long v = 20230611133921525l; // 0l; // snapshot version for e.g. 20230915162636001l;
        int[] shardingFactorArray = {2, 4, 8, 16, 32, 64, 128, 256, 512, 1024}; //

        HollowFilesystemBlobRetriever hollowBlobRetriever = new HollowFilesystemBlobRetriever(Paths.get(blobPath));
        HollowConsumer c = HollowConsumer
                .withBlobRetriever(hollowBlobRetriever).build();
        c.triggerRefreshTo(v);

        for (HollowTypeReadState typeReadState : c.getStateEngine().getTypeStates()) {
            for (int shardingFactor : shardingFactorArray) {
                if (blobPath==null || v==0l) {
                    throw new IllegalArgumentException("These arguments need to be specified");
                }

                HollowChecksum origChecksum = typeReadState.getChecksum(typeReadState.getSchema());

                HollowTypeReshardingStrategy reshardingStrategy = HollowTypeReshardingStrategy.getInstance(typeReadState);
                reshardingStrategy.reshard(typeReadState, typeReadState.numShards(), shardingFactor * typeReadState.numShards());
                reshardingStrategy.reshard(typeReadState, typeReadState.numShards(), typeReadState.numShards() / shardingFactor);
                HollowChecksum joinedChecksum = typeReadState.getChecksum(typeReadState.getSchema());

                assertEquals(joinedChecksum, origChecksum);
                System.out.println("Processed type " + typeReadState.getSchema().getName() + " with " + typeReadState.numShards() + " shard and sharding factor " + shardingFactor);
            }
        }
    }
}
