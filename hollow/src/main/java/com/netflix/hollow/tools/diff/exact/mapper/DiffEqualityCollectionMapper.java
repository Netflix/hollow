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
package com.netflix.hollow.tools.diff.exact.mapper;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowCollectionTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowCollectionSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap.OrdinalIdentityTranslator;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;

/**
 * Not intended for external consumption.
 */
public class DiffEqualityCollectionMapper extends DiffEqualityTypeMapper {

    private final boolean requiresTraversalForMissingFields;
    private final DiffEqualOrdinalMap referencedTypeEqualOrdinalMap;
    private final boolean orderingIsImportant;

    public DiffEqualityCollectionMapper(DiffEqualityMapping mapping, HollowCollectionTypeReadState fromState, HollowCollectionTypeReadState toState, boolean oneToOne) {
        this(mapping, fromState, toState, oneToOne, false);
    }

    public DiffEqualityCollectionMapper(DiffEqualityMapping mapping, HollowCollectionTypeReadState fromState, HollowCollectionTypeReadState toState, boolean oneToOne, boolean orderingIsImportant) {
        super(fromState, toState, oneToOne);
        HollowCollectionSchema schema = fromState.getSchema();
        this.referencedTypeEqualOrdinalMap = mapping.getEqualOrdinalMap(schema.getElementType());
        this.requiresTraversalForMissingFields = mapping.requiresMissingFieldTraversal(schema.getElementType());
        this.orderingIsImportant = orderingIsImportant;
    }


    public boolean requiresTraversalForMissingFields() {
        return requiresTraversalForMissingFields;
    }

    protected EqualityDeterminer getEqualityDeterminer() {
        return new EqualityDeterminer() {
            private final IntList fromIntList = new IntList();
            private final IntList toIntList = new IntList();

            public boolean recordsAreEqual(int fromOrdinal, int toOrdinal) {
                if(!populateIntList(fromIntList, fromState().ordinalIterator(fromOrdinal), referencedTypeEqualOrdinalMap.getFromOrdinalIdentityTranslator()))
                    return false;
                if(!populateIntList(toIntList, toState().ordinalIterator(toOrdinal), referencedTypeEqualOrdinalMap.getToOrdinalIdentityTranslator()))
                    return false;

                return fromIntList.equals(toIntList);
            }

            private boolean populateIntList(IntList list, HollowOrdinalIterator iter, OrdinalIdentityTranslator identityTranslator) {
                list.clear();

                int nextOrdinal = iter.next();

                while(nextOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    int identityOrdinal = identityTranslator.getIdentityOrdinal(nextOrdinal);

                    if(identityOrdinal == -1 && nextOrdinal != -1)
                        return false;

                    list.add(identityOrdinal);

                    nextOrdinal = iter.next();
                }

                if(!orderingIsImportant)
                    list.sort();
                return true;
            }
        };
    }

    protected int fromRecordHashCode(int ordinal) {
        return recordHashCode(fromState(), ordinal, referencedTypeEqualOrdinalMap.getFromOrdinalIdentityTranslator());
    }

    protected int toRecordHashCode(int ordinal) {
        return recordHashCode(toState(), ordinal, referencedTypeEqualOrdinalMap.getToOrdinalIdentityTranslator());
    }

    protected int recordHashCode(HollowCollectionTypeReadState typeState, int ordinal, OrdinalIdentityTranslator identityTranslator) {
        HollowOrdinalIterator iter = typeState.ordinalIterator(ordinal);

        int elementOrdinal = iter.next();

        int hashCode = 0;

        while(elementOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            int identityElementOrdinal = identityTranslator.getIdentityOrdinal(elementOrdinal);

            if(identityElementOrdinal == -1 && elementOrdinal != -1)
                return -1;

            hashCode ^= HashCodes.hashInt(identityElementOrdinal);
            if(hashCode == 0)
                hashCode ^= HashCodes.hashInt(identityElementOrdinal);

            elementOrdinal = iter.next();
        }

        return hashCode;
    }

    private HollowCollectionTypeReadState fromState() {
        return (HollowCollectionTypeReadState) fromState;
    }

    private HollowCollectionTypeReadState toState() {
        return (HollowCollectionTypeReadState) toState;
    }

}
