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

import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.api.objects.delegate.HollowRecordDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;
import java.util.AbstractList;

/**
 * A HollowList provides an implementation of the {@link java.util.List} interface over
 * a LIST record in a Hollow dataset.
 */
public abstract class HollowList<T> extends AbstractList<T> implements HollowRecord {

    protected final int ordinal;
    protected final HollowListDelegate<T> delegate;

    public HollowList(HollowListDelegate<T> delegate, int ordinal) {
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
    public final T get(int index) {
        return delegate.get(this, ordinal, index);
    }

    @Override
    public final boolean contains(Object o) {
        return delegate.contains(this, ordinal, o);
    }

    @Override
    public final int indexOf(Object o) {
        return delegate.indexOf(this, ordinal, o);
    }

    @Override
    public final int lastIndexOf(Object o) {
        return delegate.lastIndexOf(this, ordinal, o);
    }

    public abstract T instantiateElement(int elementOrdinal);
    public abstract boolean equalsElement(int elementOrdinal, Object testObject);

    @Override
    public HollowListSchema getSchema() {
        return delegate.getSchema();
    }

    @Override
    public HollowListTypeDataAccess getTypeDataAccess() {
        return delegate.getTypeDataAccess();
    }

    @Override
    public HollowRecordDelegate getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object o) {
        // Note: hashCode is computed from the list's contents, see AbstractList.hashCode

        if (this == o) {
            return true;
        }

        // If type state is the same then compare ordinals
        if (o instanceof HollowList) {
            HollowList<?> that = (HollowList<?>) o;
            if (delegate.getTypeDataAccess() == that.delegate.getTypeDataAccess()) {
                return ordinal == that.ordinal;
            }
        }

        // Otherwise, compare the contents
        return super.equals(o);
    }
}
