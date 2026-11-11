package com.netflix.hollow.core.read.engine;

public interface HollowTypeReadStateShard {

    HollowTypeDataElements getDataElements();

    int getShardOrdinalShift();

    /**
     * Returns the smallest ordinal greater than {@code ordinal} that belongs to shard {@code shardNumber},
     * where ordinal {@code o} belongs to shard {@code o & (numShards - 1)}. The result may not be populated,
     * so callers pass it to {@link java.util.BitSet#nextSetBit(int)} and check the shard of the bit returned.
     *
     * @param ordinal the current ordinal, in any shard
     * @param shardNumber the shard number, less than {@code numShards}
     * @param numShards the number of shards, a power of 2
     * @return the next ordinal after {@code ordinal} in shard {@code shardNumber}
     */
    static int nextOrdinalInShard(int ordinal, int shardNumber, int numShards) {
        int r = (ordinal & -numShards) + shardNumber;
        return r <= ordinal ? r + numShards : r;
    }
}
