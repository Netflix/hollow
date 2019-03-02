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

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;

/**
 * This is a {@link HollowOrdinalIterator} which only will iterate over the potential matches for a specific hash code.
 * 
 * Once this iterator encounters an empty bucket, the iteration ends.
 */
public class PotentialMatchHollowSetOrdinalIterator implements HollowOrdinalIterator {

    private final int setOrdinal;
    private final HollowSetTypeDataAccess dataAccess;
    private final int numBuckets;
    private int currentBucket;

    public PotentialMatchHollowSetOrdinalIterator(int setOrdinal, HollowSetTypeDataAccess dataAccess, int hashCode) {
        this.setOrdinal = setOrdinal;
        this.dataAccess = dataAccess;
        this.numBuckets = HashCodes.hashTableSize(dataAccess.size(setOrdinal));
        this.currentBucket = HashCodes.hashInt(hashCode) & (numBuckets - 1);
    }

    @Override
    public int next() {
        int currentBucketValue;

        currentBucketValue = dataAccess.relativeBucketValue(setOrdinal, currentBucket);
        if(currentBucketValue == ORDINAL_NONE) {
            return NO_MORE_ORDINALS;
        }

        currentBucket++;
        currentBucket &= (numBuckets - 1);

        return currentBucketValue;
    }

}
