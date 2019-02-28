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

import com.netflix.hollow.api.objects.delegate.HollowRecordDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A HollowSet provides an implementation of the {@link java.util.Set} interface over
 * a SET record in a Hollow dataset.
 * 
 * Also provides the findElement() method, which allows for searching the set for elements with matching hash keys.
 */
public abstract class HollowSet<T> extends AbstractSet<T> implements HollowRecord {

    protected final int ordinal;
    protected final HollowSetDelegate<T> delegate;

    public HollowSet(HollowSetDelegate<T> delegate, int ordinal) {
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
    public boolean contains(Object o) {
        return delegate.contains(this, ordinal, o);
    }
    
    /**
     * Find an element with the specified hash key. 
     * 
     * @param hashKey The hash key to match.
     * @return The element if discovered, null otherwise.
     */
    public T findElement(Object... hashKey) {
        return delegate.findElement(this, ordinal, hashKey);
    }

    public abstract T instantiateElement(int elementOrdinal);
    public abstract boolean equalsElement(int elementOrdinal, Object testObject);

    @Override
    public HollowSetSchema getSchema() {
        return delegate.getSchema();
    }

    @Override
    public HollowSetTypeDataAccess getTypeDataAccess() {
        return delegate.getTypeDataAccess();
    }

    @Override
    public final Iterator<T> iterator() {
        return new Itr();
    }

    @Override
    public HollowRecordDelegate getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object o) {
        // Note: hashCode is computed from the set's contents, see AbstractSet.hashCode

        if (this == o) {
            return true;
        }

        // If type state is the same then compare ordinals
        if (o instanceof HollowSet) {
            HollowSet<?> that = (HollowSet<?>) o;
            if (delegate.getTypeDataAccess() == that.delegate.getTypeDataAccess()) {
                return ordinal == that.ordinal;
            }
        }

        // Otherwise, compare the contents
        return super.equals(o);
    }

    private final class Itr implements Iterator<T> {

        private final HollowOrdinalIterator ordinalIterator;
        private T next;

        Itr() {
            this.ordinalIterator = delegate.iterator(ordinal);
            positionNext();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            T current = next;
            positionNext();
            return current;
        }

        private void positionNext() {
            int currentOrdinal = ordinalIterator.next();

            if(currentOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS)
                next = instantiateElement(currentOrdinal);
            else
                next = null;
        }
    }
}
