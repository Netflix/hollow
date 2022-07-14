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
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.util.IntList;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap.OrdinalIdentityTranslator;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;

/**
 * Not intended for external consumption.
 */
public class DiffEqualityMapMapper extends DiffEqualityTypeMapper {

    private final DiffEqualOrdinalMap keyEqualOrdinalMap;
    private final DiffEqualOrdinalMap valueEqualOrdinalMap;

    private final boolean requiresTraversalForMissingFields;

    public DiffEqualityMapMapper(DiffEqualityMapping mapping, HollowMapTypeReadState fromState, HollowMapTypeReadState toState, boolean oneToOne) {
        super(fromState, toState, oneToOne);

        HollowMapSchema schema = fromState.getSchema();

        this.keyEqualOrdinalMap = mapping.getEqualOrdinalMap(schema.getKeyType());
        this.valueEqualOrdinalMap = mapping.getEqualOrdinalMap(schema.getValueType());

        this.requiresTraversalForMissingFields =
                mapping.requiresMissingFieldTraversal(schema.getKeyType())
                        || mapping.requiresMissingFieldTraversal(schema.getValueType());
    }

    @Override
    public boolean requiresTraversalForMissingFields() {
        return requiresTraversalForMissingFields;
    }

    @Override
    protected int fromRecordHashCode(int ordinal) {
        return recordHashCode(fromState(), ordinal, keyEqualOrdinalMap.getFromOrdinalIdentityTranslator(), valueEqualOrdinalMap.getFromOrdinalIdentityTranslator());
    }

    @Override
    protected int toRecordHashCode(int ordinal) {
        return recordHashCode(toState(), ordinal, keyEqualOrdinalMap.getToOrdinalIdentityTranslator(), valueEqualOrdinalMap.getToOrdinalIdentityTranslator());
    }

    private int recordHashCode(HollowMapTypeReadState typeState, int ordinal, OrdinalIdentityTranslator keyTranslator, OrdinalIdentityTranslator valueTranslator) {
        HollowMapEntryOrdinalIterator iter = typeState.ordinalIterator(ordinal);

        int hashCode = 0;

        while(iter.next()) {
            int keyIdentityOrdinal = keyTranslator.getIdentityOrdinal(iter.getKey());
            int valueIdentityOrdinal = valueTranslator.getIdentityOrdinal(iter.getValue());

            if(keyIdentityOrdinal == -1 && iter.getKey() != -1)
                return -1;
            if(valueIdentityOrdinal == -1 && iter.getValue() != -1)
                return -1;

            hashCode ^= HashCodes.hashInt(keyIdentityOrdinal + (31 * valueIdentityOrdinal));
        }

        return hashCode;
    }

    @Override
    protected EqualityDeterminer getEqualityDeterminer() {
        return new EqualityDeterminer() {
            private final IntList fromKeysIntList = new IntList();
            private final IntList fromValuesIntList = new IntList();
            private final IntList toKeysIntList = new IntList();
            private final IntList toValuesIntList = new IntList();

            @Override
            public boolean recordsAreEqual(int fromOrdinal, int toOrdinal) {
                if(!populateIntLists(fromKeysIntList, fromValuesIntList, fromState().ordinalIterator(fromOrdinal), keyEqualOrdinalMap.getFromOrdinalIdentityTranslator(), valueEqualOrdinalMap.getFromOrdinalIdentityTranslator()))
                    return false;
                if(!populateIntLists(toKeysIntList, toValuesIntList, toState().ordinalIterator(toOrdinal), keyEqualOrdinalMap.getToOrdinalIdentityTranslator(), valueEqualOrdinalMap.getToOrdinalIdentityTranslator()))
                    return false;

                return fromKeysIntList.equals(toKeysIntList) && fromValuesIntList.equals(toValuesIntList);
            }

            private boolean populateIntLists(IntList keysList, IntList valuesList, HollowMapEntryOrdinalIterator iter, OrdinalIdentityTranslator keyTranslator, OrdinalIdentityTranslator valueTranslator) {
                keysList.clear();
                valuesList.clear();

                while(iter.next()) {
                    int keyIdentity = keyTranslator.getIdentityOrdinal(iter.getKey());
                    int valueIdentity = valueTranslator.getIdentityOrdinal(iter.getValue());

                    if(keyIdentity == -1 && iter.getKey() != -1)
                        return false;
                    if(valueIdentity == -1 && iter.getValue() != -1)
                        return false;

                    keysList.add(keyTranslator.getIdentityOrdinal(iter.getKey()));
                    valuesList.add(valueTranslator.getIdentityOrdinal(iter.getValue()));
                }

                keysList.sort();
                valuesList.sort();

                return true;
            }
        };
    }

    private HollowMapTypeReadState fromState() {
        return (HollowMapTypeReadState) fromState;
    }

    private HollowMapTypeReadState toState() {
        return (HollowMapTypeReadState) toState;
    }

}
