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

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HollowPerfBackedSet<T> extends AbstractSet<T> {
    private final int ordinal;
    private final HollowSetTypeDataAccess dataAccess;
    private final long elementMaskedTypeIdx;
    private final POJOInstantiator<T> instantiator;
    private final HashKeyExtractor hashKeyExtractor;

    public HollowPerfBackedSet(
            HollowSetTypePerfAPI typeApi, 
            long ref,
            POJOInstantiator<T> instantiator,
            HashKeyExtractor hashKeyExtractor) {
        this.dataAccess = typeApi.typeAccess();
        this.ordinal = typeApi.ordinal(ref);
        this.instantiator = instantiator;
        this.elementMaskedTypeIdx = typeApi.elementMaskedTypeIdx;
        this.hashKeyExtractor = hashKeyExtractor;
    }

    @Override
    public Iterator<T> iterator() {
        HollowOrdinalIterator oi = dataAccess.ordinalIterator(ordinal);

        return new Iterator<T>() {
            int eo = oi.next();

            @Override public boolean hasNext() {
                return eo != HollowOrdinalIterator.NO_MORE_ORDINALS;
            }

            @Override public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                int o = eo;
                eo = oi.next();
                return instantiator.instantiate(elementMaskedTypeIdx | o);
            }
        };
    }

    @Override
    public boolean contains(Object o) {
        if(hashKeyExtractor == null)
            throw new UnsupportedOperationException();
        
        Object[] key = hashKeyExtractor.extractArray(o);
        if(key == null)
            return false;
        return dataAccess.findElement(ordinal, key) != -1;
    }

    @Override
    public int size() {
        return dataAccess.size(ordinal);
    }

}
