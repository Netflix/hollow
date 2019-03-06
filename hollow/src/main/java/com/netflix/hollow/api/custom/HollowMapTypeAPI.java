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
package com.netflix.hollow.api.custom;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;

import java.util.Map;

/**
 * This is the Hollow Type API interface for MAP type records. 
 * 
 * @see HollowTypeAPI
 */
public class HollowMapTypeAPI<K, V> extends HollowTypeAPI implements HollowMapDelegate<K,V> {

    public HollowMapTypeAPI(HollowMapTypeDataAccess typeDataAccess) {
        super(typeDataAccess);
    }
    
    public int size(int ordinal) {
        return getTypeDataAccess().size(ordinal);
    }
    
    public int get(int ordinal, int keyOrdinal) {
        return getTypeDataAccess().get(ordinal, keyOrdinal);
    }

    public int get(int ordinal, int keyOrdinal, int hashCode) {
        return getTypeDataAccess().get(ordinal, keyOrdinal, hashCode);
    }
    
    public int findKey(int ordinal, Object... hashKey) {
        return getTypeDataAccess().findKey(ordinal, hashKey);
    }
    
    public int findValue(int ordinal, Object... hashKey) {
        return getTypeDataAccess().findValue(ordinal, hashKey);
    }
    
    public long findEntry(int ordinal, Object... hashKey) {
        return getTypeDataAccess().findEntry(ordinal, hashKey);
    }

    public HollowMapEntryOrdinalIterator getOrdinalIterator(int ordinal) {
        return getTypeDataAccess().ordinalIterator(ordinal);
    }

    public HollowMapEntryOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        return getTypeDataAccess().potentialMatchOrdinalIterator(ordinal, hashCode);
    }
    
    @Override
    public HollowMapTypeDataAccess getTypeDataAccess() {
        return (HollowMapTypeDataAccess) typeDataAccess;
    }

    @Override
    public V get(HollowMap<K, V> map, int ordinal, Object key) {
        HollowMapEntryOrdinalIterator iter;

        if(getSchema().getHashKey() != null) {
            iter = getTypeDataAccess().ordinalIterator(ordinal);
        } else {
            int hashCode = getTypeDataAccess().getDataAccess().getHashCodeFinder().hashCode(key);
            iter = getTypeDataAccess().potentialMatchOrdinalIterator(ordinal, hashCode);
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
            iter = getTypeDataAccess().ordinalIterator(ordinal);
        } else {
            int hashCode = getTypeDataAccess().getDataAccess().getHashCodeFinder().hashCode(key);
            iter = getTypeDataAccess().potentialMatchOrdinalIterator(ordinal, hashCode);
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
        int keyOrdinal = getTypeDataAccess().findKey(ordinal, hashKey);
        if(keyOrdinal != -1)
            return map.instantiateKey(keyOrdinal);
        return null;
    }

    @Override
    public V findValue(HollowMap<K, V> map, int ordinal, Object... hashKey) {
        int valueOrdinal = getTypeDataAccess().findValue(ordinal, hashKey);
        if(valueOrdinal != -1)
            return map.instantiateValue(valueOrdinal);
        return null;
    }

    @Override
    public Map.Entry<K, V> findEntry(final HollowMap<K, V> map, int ordinal, Object... hashKey) {
        final long entryOrdinals = getTypeDataAccess().findEntry(ordinal, hashKey);
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
        return getTypeDataAccess().ordinalIterator(ordinal);
    }

    @Override
    public HollowMapSchema getSchema() {
        return getTypeDataAccess().getSchema();
    }

    @Override
    public HollowMapTypeAPI getTypeAPI() {
        return this;
    }

}
