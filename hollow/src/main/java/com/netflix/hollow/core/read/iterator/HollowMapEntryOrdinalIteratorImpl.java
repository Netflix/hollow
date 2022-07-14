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
package com.netflix.hollow.core.read.iterator;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;

public class HollowMapEntryOrdinalIteratorImpl implements HollowMapEntryOrdinalIterator {

    private final int mapOrdinal;
    private final HollowMapTypeDataAccess dataAccess;
    private final int numBuckets;
    private int currentBucket = -1;

    private int key;
    private int value;

    public HollowMapEntryOrdinalIteratorImpl(int mapOrdinal, HollowMapTypeDataAccess dataAccess) {
        this.mapOrdinal = mapOrdinal;
        this.dataAccess = dataAccess;
        this.numBuckets = HashCodes.hashTableSize(dataAccess.size(mapOrdinal));
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public int getValue() {
        return value;
    }

    public int getCurrentBucket() {
        return currentBucket;
    }

    @Override
    public boolean next() {
        key = -1;

        while(key == -1) {
            currentBucket++;
            if(currentBucket >= numBuckets)
                return false;

            long bucketVal = dataAccess.relativeBucket(mapOrdinal, currentBucket);
            this.key = (int) (bucketVal >>> 32);
            this.value = (int) bucketVal;
        }

        return true;
    }

}
