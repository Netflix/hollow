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
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import java.util.Map;

public class HollowMapTypePerfAPI extends HollowTypePerfAPI {

    private final HollowMapTypeDataAccess typeAccess;
    final long keyMaskedTypeIdx;
    final long valueMaskedTypeIdx;

    public HollowMapTypePerfAPI(HollowDataAccess dataAccess, String typeName, HollowPerformanceAPI api) {
        super(typeName, api);

        HollowMapTypeDataAccess typeAccess = (HollowMapTypeDataAccess) dataAccess.getTypeDataAccess(typeName);

        int keyTypeIdx = typeAccess == null ? Ref.TYPE_ABSENT : api.types.getIdx(typeAccess.getSchema().getKeyType());
        int valueTypeIdx = typeAccess == null ? Ref.TYPE_ABSENT : api.types.getIdx(typeAccess.getSchema().getValueType());

        this.keyMaskedTypeIdx = Ref.toTypeMasked(keyTypeIdx);
        this.valueMaskedTypeIdx = Ref.toTypeMasked(valueTypeIdx);

        if(typeAccess == null)
            typeAccess = new HollowMapMissingDataAccess(dataAccess, typeName);
        this.typeAccess = typeAccess;
    }

    public int size(long ref) {
        return typeAccess.size(ordinal(ref));
    }

    public HollowPerfMapEntryIterator possibleMatchIter(long ref, int hashCode) {
        HollowMapEntryOrdinalIterator iter = typeAccess.potentialMatchOrdinalIterator(ordinal(ref), hashCode);
        return new HollowPerfMapEntryIterator(iter, keyMaskedTypeIdx, valueMaskedTypeIdx);
    }

    public HollowPerfMapEntryIterator iterator(long ref) {
        HollowMapEntryOrdinalIterator iter = typeAccess.ordinalIterator(ordinal(ref));
        return new HollowPerfMapEntryIterator(iter, keyMaskedTypeIdx, valueMaskedTypeIdx);
    }

    public long findKey(long ref, Object... hashKey) {
        int ordinal = typeAccess.findKey(ordinal(ref), hashKey);
        return Ref.toRefWithTypeMasked(keyMaskedTypeIdx, ordinal);
    }

    public long findValue(long ref, Object... hashKey) {
        int ordinal = typeAccess.findValue(ordinal(ref), hashKey);
        return Ref.toRefWithTypeMasked(valueMaskedTypeIdx, ordinal);
    }

    public <K, V> Map<K, V> backedMap(long ref, POJOInstantiator<K> keyInstantiator, POJOInstantiator<V> valueInstantiator, HashKeyExtractor hashKeyExtractor) {
        return new HollowPerfBackedMap<K, V>(this, ordinal(ref), keyInstantiator, valueInstantiator, hashKeyExtractor);
    }

    public HollowMapTypeDataAccess typeAccess() {
        return typeAccess;
    }

}
