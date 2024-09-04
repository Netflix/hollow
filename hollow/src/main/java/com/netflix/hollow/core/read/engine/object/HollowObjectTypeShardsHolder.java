package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.ShardsHolder;

public class HollowObjectTypeShardsHolder extends ShardsHolder {
    final HollowObjectTypeReadStateShard shards[];
    final int shardNumberMask;

    /**
     * Thread safe construction of ShardHolder with given shards
     * @param fromShards shards to be used
     */
    public HollowObjectTypeShardsHolder(HollowTypeReadStateShard[] fromShards) {
        this.shards = new HollowObjectTypeReadStateShard[fromShards.length];
        for (int i=0; i<fromShards.length; i++) {
            this.shards[i] = (HollowObjectTypeReadStateShard) fromShards[i];
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
    HollowObjectTypeShardsHolder(HollowObjectTypeReadStateShard[] oldShards, HollowObjectTypeReadStateShard newShard, int newShardIndex) {
        int numShards = oldShards.length;
        HollowObjectTypeReadStateShard[] shards = new HollowObjectTypeReadStateShard[numShards];
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

    @Override
    public int getShardNumberMask() {
        return shardNumberMask;
    }
}
