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
package com.netflix.hollow.api.objects.delegate;

import com.netflix.hollow.api.custom.HollowMapTypeAPI;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for cached MAP type records.
 * 
 * @see HollowRecordDelegate
 */
public class HollowMapCachedDelegate<K, V> implements HollowMapDelegate<K, V>, HollowCachedDelegate {

    private final int ordinals[];
    private final int hashMask;
    private final int size;
    protected HollowMapTypeAPI typeAPI;
    private HollowMapTypeDataAccess dataAccess;

    public HollowMapCachedDelegate(HollowMapTypeDataAccess dataAccess, int ordinal) {
        this(dataAccess, null, ordinal);
    }

    public HollowMapCachedDelegate(HollowMapTypeAPI typeAPI, int ordinal) {
        this(typeAPI.getTypeDataAccess(), typeAPI, ordinal);
    }

    private HollowMapCachedDelegate(HollowMapTypeDataAccess dataAccess, HollowMapTypeAPI typeAPI, int ordinal) {
        int size = dataAccess.size(ordinal);

        int ordinals[] = new int[HashCodes.hashTableSize(size) * 2];

        for(int i = 0; i < ordinals.length; i += 2) {
            long bucketData = dataAccess.relativeBucket(ordinal, i / 2);

            ordinals[i] = (int) (bucketData >> 32);
            ordinals[i + 1] = (int) bucketData;
        }

        this.ordinals = ordinals;
        this.hashMask = (ordinals.length / 2) - 1;
        this.size = size;
        this.dataAccess = dataAccess;
        this.typeAPI = typeAPI;
    }

    @Override
    public int size(int ordinal) {
        return size;
    }

    @Override
    public V get(HollowMap<K, V> map, int ordinal, Object key) {
        if(getSchema().getHashKey() != null) {
            for(int i = 0; i < ordinals.length; i += 2) {
                if(ordinals[i] != -1 && map.equalsKey(ordinals[i], key))
                    return map.instantiateValue(ordinals[i + 1]);
            }
        } else {
            int hashCode = dataAccess.getDataAccess().getHashCodeFinder().hashCode(key);
            int bucket = (HashCodes.hashInt(hashCode) & hashMask) * 2;

            while(ordinals[bucket] != -1) {
                if(map.equalsKey(ordinals[bucket], key)) {
                    return map.instantiateValue(ordinals[bucket + 1]);
                }

                bucket += 2;
                bucket &= ordinals.length - 1;
            }
        }

        return null;
    }

    @Override
    public boolean containsKey(HollowMap<K, V> map, int ordinal, Object key) {
        if(getSchema().getHashKey() != null) {
            for(int i = 0; i < ordinals.length; i += 2) {
                if(ordinals[i] != -1 && map.equalsKey(ordinals[i], key))
                    return true;
            }
        } else {
            int hashCode = dataAccess.getDataAccess().getHashCodeFinder().hashCode(key);
            int bucket = (HashCodes.hashInt(hashCode) & hashMask) * 2;

            while(ordinals[bucket] != -1) {
                if(map.equalsKey(ordinals[bucket], key)) {
                    return true;
                }

                bucket += 2;
                bucket &= ordinals.length - 1;
            }
        }

        return false;
    }

    @Override
    public boolean containsValue(HollowMap<K, V> map, int ordinal, Object value) {
        HollowMapEntryOrdinalIterator iter = iterator(ordinal);
        while(iter.next()) {
            if(map.equalsValue(iter.getValue(), value))
                return true;
        }
        return false;
    }

    @Override
    public K findKey(HollowMap<K, V> map, int ordinal, Object... hashKey) {
        int keyOrdinal = dataAccess.findKey(ordinal, hashKey);
        if(keyOrdinal != -1)
            return map.instantiateKey(keyOrdinal);
        return null;
    }

    @Override
    public V findValue(HollowMap<K, V> map, int ordinal, Object... hashKey) {
        int valueOrdinal = dataAccess.findValue(ordinal, hashKey);
        if(valueOrdinal != -1)
            return map.instantiateValue(valueOrdinal);
        return null;
    }

    @Override
    public Entry<K, V> findEntry(final HollowMap<K, V> map, int ordinal, Object... hashKey) {
        final long entryOrdinals = dataAccess.findEntry(ordinal, hashKey);
        if(entryOrdinals != -1L)
            return new Map.Entry<K, V>() {
                @Override
                public K getKey() {
                    return map.instantiateKey((int) (entryOrdinals >> 32));
                }

                @Override
                public V getValue() {
                    return map.instantiateValue((int) entryOrdinals);
                }

                @Override
                public V setValue(V value) {
                    throw new UnsupportedOperationException();
                }
            };

        return null;
    }

    @Override
    public HollowMapEntryOrdinalIterator iterator(int ordinal) {
        return new HollowMapEntryOrdinalIterator() {
            private int bucket = -2;

            @Override
            public boolean next() {
                do {
                    bucket += 2;
                } while(bucket < ordinals.length && ordinals[bucket] == -1);
                return bucket < ordinals.length;
            }

            @Override
            public int getValue() {
                return ordinals[bucket + 1];
            }

            @Override
            public int getKey() {
                return ordinals[bucket];
            }
        };

    }

    @Override
    public HollowMapSchema getSchema() {
        return dataAccess.getSchema();
    }

    @Override
    public HollowMapTypeDataAccess getTypeDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowMapTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (HollowMapTypeAPI) typeAPI;
        this.dataAccess = this.typeAPI.getTypeDataAccess();
    }

}
