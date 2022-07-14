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

import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Set;

public class HollowSetTypePerfAPI extends HollowTypePerfAPI {

    private final HollowSetTypeDataAccess typeAccess;
    final long elementMaskedTypeIdx;

    public HollowSetTypePerfAPI(HollowDataAccess dataAccess, String typeName, HollowPerformanceAPI api) {
        super(typeName, api);

        HollowSetTypeDataAccess typeAccess = (HollowSetTypeDataAccess) dataAccess.getTypeDataAccess(typeName);

        int elementTypeIdx = typeAccess == null ? Ref.TYPE_ABSENT : api.types.getIdx(typeAccess.getSchema().getElementType());
        this.elementMaskedTypeIdx = Ref.toTypeMasked(elementTypeIdx);

        if(typeAccess == null)
            typeAccess = new HollowSetMissingDataAccess(dataAccess, typeName);
        this.typeAccess = typeAccess;
    }

    public int size(long ref) {
        return typeAccess.size(ordinal(ref));
    }

    public HollowPerfReferenceIterator iterator(long ref) {
        HollowOrdinalIterator iter = typeAccess.ordinalIterator(ordinal(ref));
        return new HollowPerfReferenceIterator(iter, elementMaskedTypeIdx);
    }

    public long findElement(long ref, Object... hashKey) {
        int ordinal = typeAccess.findElement(ordinal(ref), hashKey);
        return Ref.toRefWithTypeMasked(elementMaskedTypeIdx, ordinal);
    }

    public <T> Set<T> backedSet(long ref, POJOInstantiator<T> instantiator, HashKeyExtractor hashKeyExtractor) {
        return new HollowPerfBackedSet<>(this, ref, instantiator, hashKeyExtractor);
    }

    public HollowSetTypeDataAccess typeAccess() {
        return typeAccess;
    }

}
