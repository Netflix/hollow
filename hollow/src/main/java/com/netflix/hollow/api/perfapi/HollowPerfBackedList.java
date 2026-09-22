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

import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.NoSuchElementException;
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
        HollowOrdinalIterator ordinalIterator = dataAccess.ordinalIterator(ordinal);
        return new Iterator<T>() {
            private int nextOrdinal = HollowOrdinalIterator.NO_MORE_ORDINALS;
            private boolean nextOrdinalLoaded;

            @Override
            public boolean hasNext() {
                if(!nextOrdinalLoaded) {
                    nextOrdinal = ordinalIterator.next();
                    nextOrdinalLoaded = true;
                }
                return nextOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS;
            }

            @Override
            public T next() {
                if(!hasNext())
                    throw new NoSuchElementException();
                int currentOrdinal = nextOrdinal;
                nextOrdinalLoaded = false;
                return instantiator.instantiate(elementMaskedTypeIdx | currentOrdinal);
            }
        };
    }

}
