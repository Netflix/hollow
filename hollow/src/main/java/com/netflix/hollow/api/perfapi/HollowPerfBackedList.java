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

import com.netflix.hollow.api.objects.HollowListTraversal;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.RandomAccess;

public class HollowPerfBackedList<T> extends AbstractList<T> implements RandomAccess {
    
    private final int ordinal;
    private final HollowListTypeDataAccess dataAccess;
    private final long elementMaskedTypeIdx;
    private final POJOInstantiator<T> instantiator;
    
    public HollowPerfBackedList(HollowListTypePerfAPI typeAPI, int ordinal,
            POJOInstantiator<T> instantiator) {
        this.dataAccess = typeAPI.typeAccess();
        this.ordinal = ordinal;
        this.instantiator = instantiator;
        this.elementMaskedTypeIdx = typeAPI.elementMaskedTypeIdx;
    }

    @Override
    public T get(int index) {
        return instantiator.instantiate(elementMaskedTypeIdx | dataAccess.getElementOrdinal(ordinal, index));
    }

    @Override
    public int size() {
        return dataAccess.size(ordinal);
    }

    @Override
    public Iterator<T> iterator() {
        if(!HollowListTraversal.isEnabled(dataAccess))
            return super.iterator();
        return HollowListTraversal.iterator(dataAccess.ordinalIterator(ordinal), this::instantiateElement);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        // Subclasses may provide custom get() behavior instead of ordinal-based instantiation.
        if(getClass() != HollowPerfBackedList.class || !HollowListTraversal.isEnabled(dataAccess))
            return super.listIterator(index);
        HollowOrdinalIterator cursor = dataAccess.ordinalIterator(ordinal);
        return cursor instanceof HollowListOrdinalIterator
                ? HollowListTraversal.listIterator((HollowListOrdinalIterator)cursor, index, this::instantiateElement)
                : super.listIterator(index);
    }

    @Override
    public Spliterator<T> spliterator() {
        if(!HollowListTraversal.isEnabled(dataAccess))
            return super.spliterator();
        if(getClass() != HollowPerfBackedList.class) {
            // Java 8's default spliterator calls iterator(), which bypasses a subclass's get().
            return Spliterators.spliterator(super.listIterator(0), size(), Spliterator.ORDERED);
        }
        HollowOrdinalIterator cursor = dataAccess.ordinalIterator(ordinal);
        return cursor instanceof HollowListOrdinalIterator
                ? HollowListTraversal.spliterator((HollowListOrdinalIterator)cursor, this::instantiateElement)
                : super.spliterator();
    }

    private T instantiateElement(int elementOrdinal) {
        return instantiator.instantiate(elementMaskedTypeIdx | elementOrdinal);
    }

}
