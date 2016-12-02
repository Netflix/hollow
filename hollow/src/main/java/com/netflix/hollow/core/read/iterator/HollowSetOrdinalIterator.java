/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.core.read.iterator;

import com.netflix.hollow.core.memory.encoding.HashCodes;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;


public class HollowSetOrdinalIterator implements HollowOrdinalIterator {

    private final int setOrdinal;
    private final HollowSetTypeDataAccess dataAccess;
    private final int numBuckets;
    private int currentBucket = -1;

    public HollowSetOrdinalIterator(int setOrdinal, HollowSetTypeDataAccess dataAccess) {
        this.setOrdinal = setOrdinal;
        this.dataAccess = dataAccess;
        this.numBuckets = HashCodes.hashTableSize(dataAccess.size(setOrdinal));
    }

    @Override
    public int next() {
        int bucketValue;

        bucketValue = -1;
        while(bucketValue == -1) {
            currentBucket++;
            if(currentBucket >= numBuckets)
                return NO_MORE_ORDINALS;

            bucketValue = dataAccess.relativeBucketValue(setOrdinal, currentBucket);
        }

        return bucketValue;
    }

    /**
     * @return the bucket position the last ordinal was retrieved from via the call to {@link #next()}.
     */
    public int getCurrentBucket() {
        return currentBucket;
    }

}
