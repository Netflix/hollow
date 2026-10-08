/*
 *  Copyright 2026 Netflix, Inc.
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

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowListProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * Java collection adapters over a captured ordinal cursor, shared by generic and performance APIs.
 */
public final class HollowListTraversal {

    private HollowListTraversal() { }

    public static boolean isEnabled(HollowListTypeDataAccess access) {
        // Inspect only the rollout flag. Traversal must still go through the proxy for object longevity.
        while(access instanceof HollowListProxyDataAccess)
            access = (HollowListTypeDataAccess)((HollowListProxyDataAccess)access).getCurrentDataAccess();
        if(!(access instanceof HollowListTypeReadState))
            return false;
        HollowReadStateEngine engine = ((HollowListTypeReadState)access).getStateEngine();
        return engine != null && engine.isShardCursorIteratorsEnabled();
    }

    public static <T> Iterator<T> iterator(HollowOrdinalIterator source, IntFunction<T> instantiate) {
        if(source instanceof HollowListOrdinalIterator) {
            HollowListOrdinalIterator cursor = (HollowListOrdinalIterator)source;
            return cursor.size() == 0 ? Collections.emptyIterator() : listIterator(cursor, 0, instantiate);
        }
        return new Iterator<T>() {
            private int nextOrdinal = HollowOrdinalIterator.NO_MORE_ORDINALS;
            private boolean loaded;

            @Override
            public boolean hasNext() {
                if(!loaded) {
                    nextOrdinal = source.next();
                    loaded = true;
                }
                return nextOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS;
            }

            @Override
            public T next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                int ordinal = nextOrdinal;
                loaded = false;
                return instantiate.apply(ordinal);
            }
        };
    }

    public static <T> ListIterator<T> listIterator(
            HollowListOrdinalIterator source, int index, IntFunction<T> instantiate) {
        int size = source.size();
        if(index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        if(size == 0)
            return Collections.emptyListIterator();
        return new ListIterator<T>() {
            private int cursor = index;

            @Override
            public boolean hasNext() {
                return cursor < source.size();
            }

            @Override
            public T next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                T value = element(cursor);
                cursor++;
                return value;
            }

            @Override
            public boolean hasPrevious() {
                return cursor > 0;
            }

            @Override
            public T previous() {
                if(!hasPrevious())
                    throw new NoSuchElementException();
                T value = element(cursor - 1);
                cursor--;
                return value;
            }

            private T element(int elementIndex) {
                int ordinal;
                try {
                    ordinal = source.getElementOrdinal(elementIndex);
                } catch(IndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
                return instantiate.apply(ordinal);
            }

            @Override
            public int nextIndex() {
                return cursor;
            }

            @Override
            public int previousIndex() {
                return cursor - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(T value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(T value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Spliterator<T> spliterator(HollowListOrdinalIterator source, IntFunction<T> instantiate) {
        int size = source.size();
        return size == 0 ? Spliterators.emptySpliterator() : new RangeSpliterator<>(source, 0, size, instantiate);
    }

    private static final class RangeSpliterator<T> implements Spliterator<T> {
        private final HollowListOrdinalIterator source;
        private final int end;
        private final IntFunction<T> instantiate;
        private int index;

        private RangeSpliterator(HollowListOrdinalIterator source, int index, int end, IntFunction<T> instantiate) {
            this.source = source;
            this.index = index;
            this.end = end;
            this.instantiate = instantiate;
        }

        @Override
        public Spliterator<T> trySplit() {
            int midpoint = index + ((end - index) >>> 1);
            if(midpoint == index)
                return null;
            Spliterator<T> prefix = new RangeSpliterator<>(source.copy(), index, midpoint, instantiate);
            index = midpoint;
            return prefix;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            if(index >= end)
                return false;
            int ordinal;
            try {
                ordinal = source.getElementOrdinal(index);
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException(e);
            }
            index++;
            action.accept(instantiate.apply(ordinal));
            return true;
        }

        @Override
        public long estimateSize() {
            return end - index;
        }

        @Override
        public int characteristics() {
            return ORDERED | SIZED | SUBSIZED;
        }
    }
}
