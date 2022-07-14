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
package com.netflix.hollow.api.objects;

import com.netflix.hollow.api.objects.delegate.HollowMapDelegate;
import com.netflix.hollow.api.objects.delegate.HollowRecordDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * A HollowMap provides an implementation of the {@link java.util.Map} interface over
 * a MAP record in a Hollow dataset.
 */
public abstract class HollowMap<K, V> extends AbstractMap<K, V> implements HollowRecord {

    protected final int ordinal;
    protected final HollowMapDelegate<K, V> delegate;

    public HollowMap(HollowMapDelegate<K, V> delegate, int ordinal) {
        this.ordinal = ordinal;
        this.delegate = delegate;
    }

    @Override
    public final int getOrdinal() {
        return ordinal;
    }

    @Override
    public final int size() {
        return delegate.size(ordinal);
    }

    @Override
    public final Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    @Override
    public final V get(Object key) {
        return delegate.get(this, ordinal, key);
    }

    @Override
    public final boolean containsKey(Object key) {
        return delegate.containsKey(this, ordinal, key);
    }

    @Override
    public final boolean containsValue(Object value) {
        return delegate.containsValue(this, ordinal, value);
    }

    public final K findKey(Object... hashKey) {
        return delegate.findKey(this, ordinal, hashKey);
    }

    public final V findValue(Object... hashKey) {
        return delegate.findValue(this, ordinal, hashKey);
    }

    public final Map.Entry<K, V> findEntry(Object... hashKey) {
        return delegate.findEntry(this, ordinal, hashKey);
    }


    public abstract K instantiateKey(int keyOrdinal);

    public abstract V instantiateValue(int valueOrdinal);

    public abstract boolean equalsKey(int keyOrdinal, Object testObject);

    public abstract boolean equalsValue(int valueOrdinal, Object testObject);

    @Override
    public HollowMapSchema getSchema() {
        return delegate.getSchema();
    }

    @Override
    public HollowMapTypeDataAccess getTypeDataAccess() {
        return delegate.getTypeDataAccess();
    }

    @Override
    public HollowRecordDelegate getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object o) {
        // Note: hashCode is computed from the map's contents, see AbstractMap.hashCode

        if(this == o) {
            return true;
        }

        // If type state is the same then compare ordinals
        if(o instanceof HollowMap) {
            HollowMap<?, ?> that = (HollowMap<?, ?>) o;
            if(delegate.getTypeDataAccess() == that.delegate.getTypeDataAccess()) {
                return ordinal == that.ordinal;
            }
        }

        // Otherwise, compare the contents
        return super.equals(o);
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryItr();
        }

        @Override
        public int size() {
            return delegate.size(ordinal);
        }
    }

    private final class EntryItr implements Iterator<Map.Entry<K, V>> {

        private final HollowMapEntryOrdinalIterator ordinalIterator;
        private Map.Entry<K, V> next;

        EntryItr() {
            this.ordinalIterator = delegate.iterator(ordinal);
            positionNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Map.Entry<K, V> next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }

            Map.Entry<K, V> current = next;
            positionNext();
            return current;
        }

        private void positionNext() {
            if(ordinalIterator.next()) {
                next = new OrdinalEntry<>(HollowMap.this, ordinalIterator.getKey(), ordinalIterator.getValue());
            } else {
                next = null;
            }
        }
    }

    private final static class OrdinalEntry<K, V> implements Map.Entry<K, V> {
        private final HollowMap<K, V> map;
        private final int keyOrdinal;
        private final int valueOrdinal;

        OrdinalEntry(HollowMap<K, V> map, int keyOrdinal, int valueOrdinal) {
            this.map = map;
            this.keyOrdinal = keyOrdinal;
            this.valueOrdinal = valueOrdinal;
        }

        @Override
        public K getKey() {
            return map.instantiateKey(keyOrdinal);
        }

        @Override
        public V getValue() {
            return map.instantiateValue(valueOrdinal);
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }

            if(!(o instanceof Map.Entry)) {
                return false;
            }

            if(o instanceof OrdinalEntry) {
                OrdinalEntry<?, ?> that = (OrdinalEntry) o;
                if(map.delegate.getTypeDataAccess() == that.map.delegate.getTypeDataAccess()) {
                    return keyOrdinal == that.keyOrdinal &&
                            valueOrdinal == that.valueOrdinal;
                }
            }

            Map.Entry<?, ?> that = (Map.Entry) o;
            return Objects.equals(getKey(), that.getKey()) &&
                    Objects.equals(getValue(), that.getValue());
        }
    }
}
