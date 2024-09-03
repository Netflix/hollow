package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.ShardsHolder;

public class ObjectTypeShardsHolder extends ShardsHolder {
    final HollowObjectTypeReadStateShard shards[];
    final int shardNumberMask;

    public ObjectTypeShardsHolder(HollowTypeReadStateShard[] fromShards) {
        this.shards = new HollowObjectTypeReadStateShard[fromShards.length];
        for (int i=0; i<fromShards.length; i++) {
            this.shards[i] = (HollowObjectTypeReadStateShard) fromShards[i];
        }
        this.shardNumberMask = fromShards.length - 1;
    }

    // SNAP: TODO: javadoc: supports thread-safe construction
    ObjectTypeShardsHolder(HollowObjectTypeReadStateShard[] oldShards, HollowObjectTypeReadStateShard newShard, int newShardIndex) {
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
