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
import com.netflix.hollow.api.sampling.HollowMapSampler;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.engine.SetMapKeyHasher;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.tools.history.HistoricalPrimaryKeyMatcher;

final class HollowHeldMapDataAccess extends HollowHeldTypeDataAccess implements HollowMapTypeDataAccess {
    private final HollowMapTypeReadState engine;
    private HistoricalPrimaryKeyMatcher keyMatcher; // compares a copy's keys as the held version has them

    HollowHeldMapDataAccess(HollowHeldStateDataAccess dataAccess, DirectLookup.TypeLookup lookup, long position) {
        super(dataAccess, lookup, position,
                new HollowMapSampler(lookup.engine.getSchema().getName(), DisabledSamplingDirector.INSTANCE));
        this.engine = (HollowMapTypeReadState) lookup.engine;
    }

    @Override
    public HollowMapSchema getSchema() {
        return engine.getSchema();
    }

    @Override
    public int size(int ordinal) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.size(ordinal) : copy(entry).size(entry.copyOrdinal);
    }

    @Override
    public int get(int ordinal, int keyOrdinal) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.get(ordinal, keyOrdinal) : copy(entry).get(entry.copyOrdinal, keyOrdinal);
    }

    @Override
    public int get(int ordinal, int keyOrdinal, int hashCode) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.get(ordinal, keyOrdinal, hashCode)
                : copy(entry).get(entry.copyOrdinal, keyOrdinal, hashCode);
    }

    @Override
    public int findKey(int ordinal, Object... hashKey) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.findKey(ordinal, hashKey) : (int) (findCopiedEntry(entry, hashKey) >> 32);
    }

    @Override
    public int findValue(int ordinal, Object... hashKey) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.findValue(ordinal, hashKey) : (int) findCopiedEntry(entry, hashKey);
    }

    @Override
    public long findEntry(int ordinal, Object... hashKey) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.findEntry(ordinal, hashKey) : findCopiedEntry(entry, hashKey);
    }

    @Override
    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.potentialMatchOrdinalIterator(ordinal, hashCode)
                : copy(entry).potentialMatchOrdinalIterator(entry.copyOrdinal, hashCode);
    }

    @Override
    public HollowMapEntryOrdinalIterator ordinalIterator(int ordinal) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null ? engine.ordinalIterator(ordinal) : copy(entry).ordinalIterator(entry.copyOrdinal);
    }

    @Override
    public long relativeBucket(int ordinal, int bucketIndex) {
        DirectLookup.Entry entry = copyOf(ordinal);
        return entry == null
                ? engine.relativeBucket(ordinal, bucketIndex)
                : copy(entry).relativeBucket(entry.copyOrdinal, bucketIndex);
    }

    /** The key and value ordinals of the copied map's entry with the hash key, or -1 if it has none. */
    private long findCopiedEntry(DirectLookup.Entry entry, Object... hashKey) {
        if (keyMatcher == null)
            return -1L;
        HollowMapTypeReadState copy = copy(entry);
        int hashTableSize = HashCodes.hashTableSize(copy.size(entry.copyOrdinal));
        int bucket = SetMapKeyHasher.hash(hashKey, keyMatcher.getFieldTypes()) & (hashTableSize - 1);
        long keyAndValue = copy.relativeBucket(entry.copyOrdinal, bucket);
        while (keyAndValue != -1L) {
            if (keyMatcher.keyMatches((int) (keyAndValue >> 32), hashKey))
                return keyAndValue;
            bucket = (bucket + 1) & (hashTableSize - 1);
            keyAndValue = copy.relativeBucket(entry.copyOrdinal, bucket);
        }
        return -1L;
    }

    void buildKeyMatcher() {
        PrimaryKey hashKey = getSchema().getHashKey();
        if (hashKey != null && dataAccess.getSchema(hashKey.getType()) != null)
            keyMatcher = new HistoricalPrimaryKeyMatcher(dataAccess, hashKey);
    }

    private static HollowMapTypeReadState copy(DirectLookup.Entry entry) {
        return (HollowMapTypeReadState) entry.copy;
    }
}
