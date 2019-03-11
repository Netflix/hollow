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
package com.netflix.hollow.api.objects.generic;

import com.netflix.hollow.api.objects.HollowMap;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;
import java.util.Iterator;
import java.util.Map;

/**
 * This is a generic handle to a MAP type record. 
 * 
 * The Generic Hollow Object API can be used to programmatically inspect a dataset (referenced by a {@link HollowDataAccess})
 * without a custom-generated API. 
 */
public class GenericHollowMap extends HollowMap<HollowRecord, HollowRecord>{

    public GenericHollowMap(HollowDataAccess dataAccess, String type, int ordinal) {
        this((HollowMapTypeDataAccess)dataAccess.getTypeDataAccess(type, ordinal), ordinal);
    }
    
    public GenericHollowMap(HollowMapTypeDataAccess dataAccess, int ordinal) {
        this(new HollowMapLookupDelegate<HollowRecord, HollowRecord>(dataAccess), ordinal);
    }

    public GenericHollowMap(HollowMapDelegate<HollowRecord, HollowRecord> typeState, int ordinal) {
        super(typeState, ordinal);
    }
    
    @Override
    public HollowRecord instantiateKey(int keyOrdinal) {
        return GenericHollowRecordHelper.instantiate(getTypeDataAccess().getDataAccess(), getSchema().getKeyType(), keyOrdinal);
    }

    @Override
    public HollowRecord instantiateValue(int valueOrdinal) {
        return GenericHollowRecordHelper.instantiate(getTypeDataAccess().getDataAccess(), getSchema().getValueType(), valueOrdinal);
    }

    @Override
    public boolean equalsKey(int keyOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getKeyType(), keyOrdinal, testObject);
    }

    @Override
    public boolean equalsValue(int valueOrdinal, Object testObject) {
        return GenericHollowRecordHelper.equalObject(getSchema().getValueType(), valueOrdinal, testObject);
    }
    
    public <K extends HollowRecord, V extends HollowRecord> Iterable<Map.Entry<K, V>>entries() {
        return new GenericHollowMapEntryIterable<K, V>(entrySet());
    }

    @Override
    public String toString() {
        return new HollowRecordStringifier().stringify(this);
    }

    static class GenericHollowMapEntryIterable<K extends HollowRecord, V extends HollowRecord> implements Iterable<Map.Entry<K, V>> {

        private final Iterable<Map.Entry<HollowRecord, HollowRecord>> wrappedIterable;
        
        public GenericHollowMapEntryIterable(Iterable<Map.Entry<HollowRecord, HollowRecord>> wrap) {
            this.wrappedIterable = wrap;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            final Iterator<Map.Entry<HollowRecord, HollowRecord>> iter = wrappedIterable.iterator();

            return new Iterator<Map.Entry<K,V>>() {

                @Override
                public boolean hasNext() {
                    return iter.hasNext();
                }

                @Override
                @SuppressWarnings("unchecked")
                public Entry<K, V> next() {
                    Map.Entry<HollowRecord, HollowRecord> entry = iter.next();
                    return new GenericHollowMapEntry((K) entry.getKey(), (V) entry.getValue());
                }
                
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
        
        
        private class GenericHollowMapEntry implements Map.Entry<K, V> {

            private final K key;
            private final V value;
            
            public GenericHollowMapEntry(K key, V value) {
                this.key = key;
                this.value = value;
            }
            
            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
            
        }

    }
}
