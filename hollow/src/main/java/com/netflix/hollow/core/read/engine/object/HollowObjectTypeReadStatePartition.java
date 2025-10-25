/*
 *  Copyright 2016-2021 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.read.engine.object;

/**
 * Represents a partition of a {@link HollowObjectTypeReadState}. Each partition maintains its own
 * array of shards for concurrent access.
 * <p>
 * A {@link HollowObjectTypeReadState} can have up to 8 partitions, with ordinals encoded to include
 * the partition index in the first 3 bits and the partition-local ordinal in the remaining bits.
 */
public class HollowObjectTypeReadStatePartition {

    final HollowObjectTypeReadStateShard[] shards;
    final int shardNumberMask;
    final int maxOrdinal;

    /**
     * Creates a new partition with the specified shards.
     *
     * @param shards the array of shards for this partition
     * @param maxOrdinal the maximum ordinal within this partition
     */
    public HollowObjectTypeReadStatePartition(HollowObjectTypeReadStateShard[] shards, int maxOrdinal) {
        this.shards = shards;
        this.shardNumberMask = shards.length - 1;
        this.maxOrdinal = maxOrdinal;
    }

    /**
     * Gets the shard for the given partition-local ordinal.
     *
     * @param partitionOrdinal the ordinal within this partition
     * @return the shard containing this ordinal
     */
    public HollowObjectTypeReadStateShard getShard(int partitionOrdinal) {
        return shards[partitionOrdinal & shardNumberMask];
    }

    /**
     * Gets the maximum ordinal within this partition.
     *
     * @return the maximum ordinal
     */
    public int getMaxOrdinal() {
        return maxOrdinal;
    }

    /**
     * Gets the number of shards in this partition.
     *
     * @return the number of shards
     */
    public int getNumShards() {
        return shards.length;
    }

    /**
     * Gets the shard array.
     *
     * @return the array of shards
     */
    public HollowObjectTypeReadStateShard[] getShards() {
        return shards;
    }
}
