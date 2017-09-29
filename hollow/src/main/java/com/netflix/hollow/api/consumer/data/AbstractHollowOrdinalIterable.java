/*
 *  Copyright 2017 Netflix, Inc.
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
 */
package com.netflix.hollow.api.consumer.data;

import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Iterator;

public abstract class AbstractHollowOrdinalIterable<T> implements Iterable<T> {
    private final HollowOrdinalIterator iter;

    public AbstractHollowOrdinalIterable(final HollowOrdinalIterator iter) {
        this.iter = iter;
    }

    protected abstract T getData(int ordinal);

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int next = iter.next();

            @Override
            public boolean hasNext() {
                return next != HollowOrdinalIterator.NO_MORE_ORDINALS;
            }

            @Override
            public T next() {
                T obj = getData(next);
                next = iter.next();
                return obj;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}