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
import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for lookup MAP type records.
 * 
 * @see HollowRecordDelegate
 */
public class HollowMapLookupDelegate<K, V> implements HollowMapDelegate<K, V> {

    private final HollowMapTypeDataAccess dataAccess;
    protected final HollowMapTypeAPI typeAPI;

    public HollowMapLookupDelegate(HollowMapTypeDataAccess dataAccess) {
        this(dataAccess, null);
    }

    public HollowMapLookupDelegate(HollowMapTypeAPI typeAPI) {
        this(typeAPI.getTypeDataAccess(), typeAPI);
    }

    private HollowMapLookupDelegate(HollowMapTypeDataAccess dataAccess, HollowMapTypeAPI typeAPI) {
        this.dataAccess = dataAccess;
        this.typeAPI = typeAPI;
    }

    @Override
    public int size(int ordinal) {
        return dataAccess.size(ordinal);
    }

    @Override
    public V get(HollowMap<K, V> map, int ordinal, Object key) {
        HollowMapEntryOrdinalIterator iter;
        
        if(getSchema().getHashKey() != null) {
            iter = dataAccess.ordinalIterator(ordinal);
        } else {
            int hashCode = dataAccess.getDataAccess().getHashCodeFinder().hashCode(key);
            iter = dataAccess.potentialMatchOrdinalIterator(ordinal, hashCode);
        }
        
        while(iter.next()) {
            if(map.equalsKey(iter.getKey(), key))
                return map.instantiateValue(iter.getValue());
        }
        return null;
    }

    @Override
    public boolean containsKey(HollowMap<K, V> map, int ordinal, Object key) {
        HollowMapEntryOrdinalIterator iter;
        
        if(getSchema().getHashKey() != null) {
            iter = dataAccess.ordinalIterator(ordinal);
        } else {
            int hashCode = dataAccess.getDataAccess().getHashCodeFinder().hashCode(key);
            iter = dataAccess.potentialMatchOrdinalIterator(ordinal, hashCode);
        }

        while(iter.next()) {
            if(map.equalsKey(iter.getKey(), key))
                return true;
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
                    return map.instantiateKey((int)(entryOrdinals >> 32));
                }

                @Override
                public V getValue() {
                    return map.instantiateValue((int)entryOrdinals);
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
        return dataAccess.ordinalIterator(ordinal);
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

}
