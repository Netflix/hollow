package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.ShardsHolder;

public class HollowSetTypeShardsHolder implements ShardsHolder {
    final HollowSetTypeReadStateShard shards[];
    final int shardNumberMask;

    /**
     * Thread safe construction of ShardHolder with given shards
     * @param fromShards shards to be used
     */
    public HollowSetTypeShardsHolder(HollowTypeReadStateShard[] fromShards) {
        this.shards = new HollowSetTypeReadStateShard[fromShards.length];
        for (int i=0; i<fromShards.length; i++) {
            this.shards[i] = (HollowSetTypeReadStateShard) fromShards[i];
        }
        this.shardNumberMask = fromShards.length - 1;
    }

    /**
     * Thread safe construction of a ShardHolder which has all the shards from {@code oldShards} except
     * the shard at index {@code newShardIndex}, using the shard {@code newShard} at that index instead.
     * @param oldShards original shards
     * @param newShard a new shard
     * @param newShardIndex index at which to place the new shard
     */
    HollowSetTypeShardsHolder(HollowSetTypeReadStateShard[] oldShards, HollowSetTypeReadStateShard newShard, int newShardIndex) {
        int numShards = oldShards.length;
        HollowSetTypeReadStateShard[] shards = new HollowSetTypeReadStateShard[numShards];
        for (int i=0; i<numShards; i++) {
            if (i == newShardIndex) {
                shards[i] = newShard;
            } else {
                shards[i] = oldShards[i];
            }
        }
        this.shards = shards;
        this.shardNumberMask = numShards - 1;
    }

    @Override
    public HollowTypeReadStateShard[] getShards() {
        return shards;
    }
}
