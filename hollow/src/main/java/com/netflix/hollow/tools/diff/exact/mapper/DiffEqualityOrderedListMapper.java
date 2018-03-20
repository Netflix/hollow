/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.tools.diff.exact.mapper;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap.OrdinalIdentityTranslator;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;

/**
 * Not intended for external consumption.
 */
public class DiffEqualityOrderedListMapper extends DiffEqualityCollectionMapper {

    public DiffEqualityOrderedListMapper(DiffEqualityMapping mapping, HollowListTypeReadState fromState, HollowListTypeReadState toState, boolean oneToOne) {
        super(mapping, fromState, toState, oneToOne, true);
    }

    @Override
    protected int recordHashCode(HollowCollectionTypeReadState typeState, int ordinal, OrdinalIdentityTranslator identityTranslator) {
        HollowOrdinalIterator iter = typeState.ordinalIterator(ordinal);

        int elementOrdinal = iter.next();

        int hashCode = 0;

        while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            int identityElementOrdinal = identityTranslator.getIdentityOrdinal(elementOrdinal);

            if(identityElementOrdinal == -1 && elementOrdinal != -1)
                return -1;

            hashCode = 7919 * hashCode + identityElementOrdinal;

            elementOrdinal = iter.next();
        }

        return HashCodes.hashInt(hashCode);
    }

}
