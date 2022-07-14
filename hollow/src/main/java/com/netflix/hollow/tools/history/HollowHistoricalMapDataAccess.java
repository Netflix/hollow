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
package com.netflix.hollow.tools.history;

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowMapSampler;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;

public class HollowHistoricalMapDataAccess extends HollowHistoricalTypeDataAccess implements HollowMapTypeDataAccess {

    private HistoricalPrimaryKeyMatcher keyMatcher;

    public HollowHistoricalMapDataAccess(HollowHistoricalStateDataAccess dataAccess, HollowTypeReadState typeState) {
        super(dataAccess, typeState, new HollowMapSampler(typeState.getSchema().getName(), DisabledSamplingDirector.INSTANCE));
    }

    @Override
    public HollowMapSchema getSchema() {
        return (HollowMapSchema) removedRecords.getSchema();
    }

    @Override
    public int size(int ordinal) {
        sampler().recordSize();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).size(ordinal);
        return removedRecords().size(getMappedOrdinal(ordinal));
    }

    @Override
    public int get(int ordinal, int keyOrdinal) {
        sampler().recordGet();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).get(ordinal, keyOrdinal);
        return removedRecords().get(getMappedOrdinal(ordinal), keyOrdinal);
    }

    @Override
    public int get(int ordinal, int keyOrdinal, int hashCode) {
        sampler().recordGet();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).get(ordinal, keyOrdinal, hashCode);
        return removedRecords().get(getMappedOrdinal(ordinal), keyOrdinal, hashCode);
    }

    @Override
    public int findKey(int ordinal, Object... hashKey) {
        return (int) (findEntry(ordinal, hashKey) >> 32);
    }

    @Override
    public int findValue(int ordinal, Object... hashKey) {
        return (int) findEntry(ordinal, hashKey);
    }

    @Override
    public long findEntry(int ordinal, Object... hashKey) {
        sampler().recordGet();
        recordStackTrace();

        if(keyMatcher == null)
            return -1L;

        if(!ordinalIsPresent(ordinal))
            return ((HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).findEntry(ordinal, hashKey);


        ordinal = ordinalRemap.get(ordinal);

        HollowMapTypeReadState removedRecords = (HollowMapTypeReadState) getRemovedRecords();

        int hashTableSize = HashCodes.hashTableSize(removedRecords.size(ordinal));
        int hash = SetMapKeyHasher.hash(hashKey, keyMatcher.getFieldTypes());

        int bucket = hash & (hashTableSize - 1);
        long bucketOrdinals = removedRecords.relativeBucket(ordinal, bucket);
        while(bucketOrdinals != -1L) {
            if(keyMatcher.keyMatches((int) (bucketOrdinals >> 32), hashKey))
                return bucketOrdinals;

            bucket++;
            bucket &= (hashTableSize - 1);
            bucketOrdinals = removedRecords.relativeBucket(ordinal, bucket);
        }

        return -1L;
    }


    @Override
    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        sampler().recordIterator();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).potentialMatchOrdinalIterator(ordinal, hashCode);
        return removedRecords().potentialMatchOrdinalIterator(getMappedOrdinal(ordinal), hashCode);
    }

    @Override
    public HollowMapEntryOrdinalIterator ordinalIterator(int ordinal) {
        sampler().recordIterator();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).ordinalIterator(ordinal);
        return removedRecords().ordinalIterator(getMappedOrdinal(ordinal));
    }

    @Override
    public long relativeBucket(int ordinal, int bucketIndex) {
        sampler().recordBucketRetrieval();
        recordStackTrace();

        if(!ordinalIsPresent(ordinal))
            return ((HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(getSchema().getName(), ordinal)).relativeBucket(ordinal, bucketIndex);
        return removedRecords().relativeBucket(getMappedOrdinal(ordinal), bucketIndex);
    }

    private HollowMapTypeReadState removedRecords() {
        return (HollowMapTypeReadState) removedRecords;
    }

    private HollowMapSampler sampler() {
        return (HollowMapSampler) sampler;
    }

    void buildKeyMatcher() {
        PrimaryKey hashKey = getSchema().getHashKey();
        if(hashKey != null)
            this.keyMatcher = new HistoricalPrimaryKeyMatcher(getDataAccess(), hashKey);
    }

}
