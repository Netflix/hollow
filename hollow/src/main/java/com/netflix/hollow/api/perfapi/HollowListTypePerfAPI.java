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
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.List;

public class HollowListTypePerfAPI extends HollowTypePerfAPI {
    
    private final HollowListTypeDataAccess typeAccess;
    final long elementMaskedTypeIdx;
    
    public HollowListTypePerfAPI(HollowDataAccess dataAccess, String typeName, HollowPerformanceAPI api) {
        super(typeName, api);
        
        HollowListTypeDataAccess typeAccess = (HollowListTypeDataAccess) dataAccess.getTypeDataAccess(typeName); 
        
        int elementTypeIdx = typeAccess == null ? Ref.TYPE_ABSENT : api.types.getIdx(typeAccess.getSchema().getElementType());
        this.elementMaskedTypeIdx = Ref.toTypeMasked(elementTypeIdx);
        
        if(typeAccess == null)
            typeAccess = new HollowListMissingDataAccess(dataAccess, typeName);
        this.typeAccess = typeAccess;
    }
    
    public int size(long ref) {
        return typeAccess.size(ordinal(ref));
    }
    
    public long get(long ref, int idx) {
        int ordinal = typeAccess.getElementOrdinal(ordinal(ref), idx);
        return Ref.toRefWithTypeMasked(elementMaskedTypeIdx, ordinal);
    }

    public HollowPerfReferenceIterator iterator(long ref) {
        HollowOrdinalIterator iter = typeAccess.ordinalIterator(ordinal(ref));
        return new HollowPerfReferenceIterator(iter, elementMaskedTypeIdx);
    }
    
    public <T> List<T> backedList(long ref, POJOInstantiator<T> instantiator) {
        return new HollowPerfBackedList<>(this, ordinal(ref), instantiator);
    }
    
    public HollowListTypeDataAccess typeAccess() {
        return typeAccess;
    }

}
