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
package com.netflix.hollow.core.read.engine.map;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;

/**
 * This is a {@link HollowMapEntryOrdinalIterator} which only will iterate over the potential matches for a specific hash code.
 * 
 * Once this iterator encounters an empty bucket, the iteration ends.
 */
public class PotentialMatchHollowMapEntryOrdinalIteratorImpl implements HollowMapEntryOrdinalIterator {

    private final int mapOrdinal;
    private final HollowMapTypeDataAccess dataAccess;
    private final int numBuckets;
    private int currentBucket;

    private int key;
    private int value;

    public PotentialMatchHollowMapEntryOrdinalIteratorImpl(int mapOrdinal, HollowMapTypeDataAccess dataAccess, int hashCode) {
        this.mapOrdinal = mapOrdinal;
        this.dataAccess = dataAccess;
        this.numBuckets = HashCodes.hashTableSize(dataAccess.size(mapOrdinal));
        this.currentBucket = HashCodes.hashInt(hashCode) & (numBuckets - 1);
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public boolean next() {
        long currentBucketValue = dataAccess.relativeBucket(mapOrdinal, currentBucket);

        int currentBucketKey = (int) (currentBucketValue >>> 32);
        if(currentBucketKey == ORDINAL_NONE)
            return false;

        key = currentBucketKey;
        value = (int) currentBucketValue;

        currentBucket++;
        currentBucket &= numBuckets - 1;

        return true;
    }

}
