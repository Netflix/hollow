/*
 *  Copyright 2016-2019 Netflix, Inc.
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
package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;

/**
 * An ordinal iterator over a set record backed by a single validated snapshot of its shard and bucket range.
 * <p>
 * The historical per-bucket pattern re-reads {@code shardsVolatile} and executes {@code Unsafe.loadFence()}
 * twice for every bucket probed (via {@code relativeBucketValue}), plus a {@code size()} at construction — for a
 * set whose hash table has M buckets that is {@code 2M+1} fences. This iterator validates the bucket range once
 * at construction and then executes a single fence per {@link #next()} (one per returned element), allocating
 * nothing.
 * <p>
 * Correctness mirrors the list snapshot iterator: {@code startBucket}/{@code endBucket} are validated non-torn
 * before use, so every relative bucket index in {@code [0, numBuckets)} maps to an in-bounds read of the shard's
 * bucket region (garbage-but-safe if the shard's backing arrays were recycled after a concurrent delta, never
 * out-of-bounds). Each {@link #next()} scans forward to the next non-empty bucket reading from the snapshot,
 * then re-validates shard identity via the {@code loadFence} in
 * {@link HollowSetTypeReadState#readWasUnsafe}; if the shard changed, the scan is discarded and re-run against a
 * freshly established snapshot before any value is returned.
 */
public class HollowSetSnapshotOrdinalIterator implements HollowOrdinalIterator {

    private final HollowSetTypeReadState readState;
    private final int ordinal;

    private HollowSetTypeShardsHolder shardsHolder;
    private HollowSetTypeReadStateShard shard;
    private long startBucket;
    private long numBuckets;

    private int currentBucket = -1;

    public HollowSetSnapshotOrdinalIterator(int ordinal, HollowSetTypeReadState readState) {
        this.readState = readState;
        this.ordinal = ordinal;
        snapshot();
    }

    private void snapshot() {
        HollowSetTypeShardsHolder holder;
        HollowSetTypeReadStateShard s;
        long start;
        long end;
        do {
            holder = readState.shardsVolatile;
            s = holder.shards[ordinal & holder.shardNumberMask];
            int shardOrdinal = ordinal >> s.shardOrdinalShift;

            start = s.dataElements.getStartBucket(shardOrdinal);
            end = s.dataElements.getEndBucket(shardOrdinal);
        } while(readState.readWasUnsafe(holder, ordinal, s));

        this.shardsHolder = holder;
        this.shard = s;
        this.startBucket = start;
        this.numBuckets = end - start;
    }

    @Override
    public int next() {
        while(true) {
            int cb = currentBucket;
            int bucketValue;
            boolean end;
            while(true) {
                cb++;
                if(cb >= numBuckets) {
                    bucketValue = NO_MORE_ORDINALS;
                    end = true;
                    break;
                }
                // in-bounds: cb < numBuckets, and [startBucket, startBucket+numBuckets) was validated
                bucketValue = shard.dataElements.getBucketValue(startBucket + cb);
                if(bucketValue != shard.dataElements.emptyBucketValue) {
                    end = false;
                    break;
                }
            }

            if(!readState.readWasUnsafe(shardsHolder, ordinal, shard)) {
                currentBucket = cb;
                return end ? NO_MORE_ORDINALS : bucketValue;
            }

            // A delta or re-shard invalidated the snapshot mid-scan; re-establish and rescan from currentBucket.
            snapshot();
        }
    }

    /**
     * @return the relative bucket position (within this set's bucket range) that the last ordinal returned by
     * {@link #next()} was retrieved from.
     */
    public int getCurrentBucket() {
        return currentBucket;
    }

}
