/*
 *
 *  Copyright 2016 Netflix, Inc.
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

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<java.util.Map.Entry<K, V>> iterator() {
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

        private EntryItr() {
            this.ordinalIterator = delegate.iterator(ordinal);
            positionNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public java.util.Map.Entry<K, V> next() {
            Map.Entry<K, V> current = next;
            positionNext();
            return current;
        }

        private void positionNext() {
            if(ordinalIterator.next()) {
                next = new Entry(ordinalIterator.getKey(), ordinalIterator.getValue());
            } else {
                next = null;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private class Entry implements Map.Entry<K, V> {
            private final int keyOrdinal;
            private final int valueOrdinal;

            public Entry(int keyOrdinal, int valueOrdinal) {
                this.keyOrdinal = keyOrdinal;
                this.valueOrdinal = valueOrdinal;
            }

            public K getKey() {
                return instantiateKey(keyOrdinal);
            }

            public V getValue() {
                return instantiateValue(valueOrdinal);
            }

            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public HollowRecordDelegate getDelegate() {
        return delegate;
    }
}
