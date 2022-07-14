/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.perfapi;

import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class HollowPerfBackedMap<K, V> extends AbstractMap<K, V> {

    private final int ordinal;
    private final HollowMapTypeDataAccess dataAccess;
    private final long keyMaskedTypeIdx;
    private final long valueMaskedTypeIdx;
    private final POJOInstantiator<K> keyInstantiator;
    private final POJOInstantiator<V> valueInstantiator;
    private final HashKeyExtractor hashKeyExtractor;

    public HollowPerfBackedMap(
            HollowMapTypePerfAPI typeApi, int ordinal,
            POJOInstantiator<K> keyInstantiator,
            POJOInstantiator<V> valueInstantiator,
            HashKeyExtractor hashKeyExtractor) {
        this.ordinal = ordinal;
        this.dataAccess = typeApi.typeAccess();
        this.keyMaskedTypeIdx = typeApi.keyMaskedTypeIdx;
        this.valueMaskedTypeIdx = typeApi.valueMaskedTypeIdx;
        this.keyInstantiator = keyInstantiator;
        this.valueInstantiator = valueInstantiator;
        this.hashKeyExtractor = hashKeyExtractor;
    }

    @Override
    public boolean containsKey(Object o) {
        Object[] hashKey = hashKeyExtractor.extractArray(o);

        return dataAccess.findValue(ordinal, hashKey) != -1;
    }

    @Override
    public V get(Object o) {
        Object[] hashKey = hashKeyExtractor.extractArray(o);

        int valueOrdinal = dataAccess.findValue(ordinal, hashKey);

        return valueOrdinal == -1 ? null : valueInstantiator.instantiate(valueMaskedTypeIdx | valueOrdinal);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                HollowMapEntryOrdinalIterator oi = dataAccess.ordinalIterator(ordinal);

                return new Iterator<Entry<K, V>>() {
                    boolean next = oi.next();

                    @Override
                    public boolean hasNext() {
                        return next;
                    }

                    @Override
                    public Entry<K, V> next() {
                        if(!hasNext()) {
                            throw new NoSuchElementException();
                        }

                        long kRef = keyMaskedTypeIdx | oi.getKey();
                        long vRef = valueMaskedTypeIdx | oi.getValue();
                        Entry<K, V> e = new BackedEntry(kRef, vRef);
                        next = oi.next();
                        return e;
                    }
                };
            }

            @Override
            public int size() {
                return HollowPerfBackedMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                if(!(o instanceof Map.Entry)) {
                    return false;
                }

                Entry<?, ?> e = (Entry<?, ?>) o;
                Object[] hashKey = hashKeyExtractor.extractArray(e.getKey());
                int valueOrdinal = dataAccess.findValue(ordinal, hashKey);

                if(valueOrdinal != -1) {
                    V iV = valueInstantiator.instantiate(valueMaskedTypeIdx | valueOrdinal);
                    if(Objects.equals(iV, e.getValue()))
                        return true;
                }

                return false;
            }
        };
    }

    @Override
    public int size() {
        return dataAccess.size(ordinal);
    }

    final class BackedEntry implements Entry<K, V> {
        final long kRef;
        final long vRef;

        // Lazily initialized on first access
        boolean kInstantiated;
        K k;
        boolean vInstantiated;
        V v;

        BackedEntry(long kRef, long vRef) {
            this.kRef = kRef;
            this.vRef = vRef;
        }

        @Override
        public K getKey() {
            if(!kInstantiated) {
                kInstantiated = true;
                k = keyInstantiator.instantiate(kRef);
            }
            return k;
        }

        @Override
        public V getValue() {
            if(!vInstantiated) {
                vInstantiated = true;
                v = valueInstantiator.instantiate(vRef);
            }
            return v;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof Map.Entry)) {
                return false;
            }
            Entry<?, ?> e = (Entry<?, ?>) o;
            return Objects.equals(getKey(), e.getKey()) && Objects.equals(getValue(), e.getValue());
        }

        @Override
        public int hashCode() {
            K key = getKey();
            V value = getValue();
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public String toString() {
            return getKey() + "=" + getValue();
        }
    }

}
