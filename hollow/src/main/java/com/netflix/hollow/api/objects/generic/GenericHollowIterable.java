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

import com.netflix.hollow.api.objects.HollowRecord;
import java.util.Iterator;

class GenericHollowIterable<T extends HollowRecord> implements Iterable<T> {

    private final Iterable<HollowRecord> wrappedIterable;

    GenericHollowIterable(Iterable<HollowRecord> wrap) {
        this.wrappedIterable = wrap;
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<HollowRecord> iter = wrappedIterable.iterator();

        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                return (T) iter.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
