/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.client.longevity;

import com.netflix.hollow.api.sampling.DisabledSamplingDirector;
import com.netflix.hollow.api.sampling.HollowSetSampler;
import com.netflix.hollow.core.HollowConstants;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.tools.history.HistoricalPrimaryKeyMatcher;

final class HollowHeldSetDataAccess extends HollowHeldTypeDataAccess implements HollowSetTypeDataAccess {
    private final HollowSetTypeReadState engine;
    private HistoricalPrimaryKeyMatcher keyMatcher; // compares a copy's elements as the held version has them

    HollowHeldSetDataAccess(HollowHeldStateDataAccess dataAccess, DirectLookup.TypeLookup lookup, long position) {
        super(dataAccess, lookup, position,
                new HollowSetSampler(lookup.engine.getSchema().getName(), DisabledSamplingDirector.INSTANCE));
        this.engine = (HollowSetTypeReadState) lookup.engine;
    }

    @Override
    public HollowSetSchema getSchema() {
        return engine.getSchema();
    }

    @Override
    public int size(int ordinal) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.size(ordinal) : copy(entry).size(entry.copyOrdinal);
    }

    @Override
    public boolean contains(int ordinal, int value) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.contains(ordinal, value) : copy(entry).contains(entry.copyOrdinal, value);
    }

    @Override
    public boolean contains(int ordinal, int value, int hashCode) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.contains(ordinal, value, hashCode)
                : copy(entry).contains(entry.copyOrdinal, value, hashCode);
    }

    @Override
    public int findElement(int ordinal, Object... hashKey) {
        DirectLookup.Entry entry = copyOf(ordinal);
        if (entry == null)
            return engine.findElement(ordinal, hashKey);
        if (keyMatcher == null)
            return HollowConstants.ORDINAL_NONE;
        HollowSetTypeReadState copy = copy(entry);
        int hashTableSize = HashCodes.hashTableSize(copy.size(entry.copyOrdinal));
        int bucket = SetMapKeyHasher.hash(hashKey, keyMatcher.getFieldTypes()) & (hashTableSize - 1);
        int element = copy.relativeBucketValue(entry.copyOrdinal, bucket);
        while (element != HollowConstants.ORDINAL_NONE) {
            if (keyMatcher.keyMatches(element, hashKey))
                return element;
            bucket = (bucket + 1) & (hashTableSize - 1);
            element = copy.relativeBucketValue(entry.copyOrdinal, bucket);
        }
        return HollowConstants.ORDINAL_NONE;
    }

    @Override
    public int relativeBucketValue(int ordinal, int bucketIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.relativeBucketValue(ordinal, bucketIndex)
                : copy(entry).relativeBucketValue(entry.copyOrdinal, bucketIndex);
    }

    @Override
    public HollowOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.potentialMatchOrdinalIterator(ordinal, hashCode)
                : copy(entry).potentialMatchOrdinalIterator(entry.copyOrdinal, hashCode);
    }

    @Override
    public HollowOrdinalIterator ordinalIterator(int ordinal) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.ordinalIterator(ordinal) : copy(entry).ordinalIterator(entry.copyOrdinal);
    }

    void buildKeyMatcher() {
        PrimaryKey hashKey = getSchema().getHashKey();
        if (hashKey != null && dataAccess.getSchema(hashKey.getType()) != null)
            keyMatcher = new HistoricalPrimaryKeyMatcher(dataAccess, hashKey);
    }

    private static HollowSetTypeReadState copy(DirectLookup.Entry entry) {
        return (HollowSetTypeReadState) entry.copy;
    }
}
