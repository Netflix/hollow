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
import com.netflix.hollow.core.read.HollowReadFieldUtils;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.tools.diff.exact.DiffEqualOrdinalMap;
import com.netflix.hollow.tools.diff.exact.DiffEqualityMapping;

/**
 * Not intended for external consumption.
 */
public class DiffEqualityObjectMapper extends DiffEqualityTypeMapper {

    private final HollowObjectSchema commonSchema;
    private final int[] toSchemaCommonFieldMapping;
    private final int[] fromSchemaCommonFieldMapping;
    private final boolean requiresTraversalForMissingFields;

    private final DiffEqualOrdinalMap[] commonReferenceFieldEqualOrdinalMaps;

    public DiffEqualityObjectMapper(DiffEqualityMapping mapping, HollowObjectTypeReadState fromState, HollowObjectTypeReadState toState, boolean oneToOne) {
        super(fromState, toState, oneToOne);
        this.commonSchema = fromState.getSchema().findCommonSchema(toState.getSchema());

        this.commonReferenceFieldEqualOrdinalMaps = new DiffEqualOrdinalMap[commonSchema.numFields()];

        for(int i = 0; i < commonReferenceFieldEqualOrdinalMaps.length; i++) {
            if(commonSchema.getFieldType(i) == FieldType.REFERENCE)
                this.commonReferenceFieldEqualOrdinalMaps[i] = mapping.getEqualOrdinalMap(commonSchema.getReferencedType(i));
        }

        this.fromSchemaCommonFieldMapping = buildCommonSchemaFieldMapping(fromState);
        this.toSchemaCommonFieldMapping = buildCommonSchemaFieldMapping(toState);

        boolean requiresTraversalForMissingFields =
                fromState().getSchema().numFields() != commonSchema.numFields()
                        || toState().getSchema().numFields() != commonSchema.numFields();

        for(int i = 0; i < commonSchema.numFields(); i++) {
            if(commonSchema.getFieldType(i) == FieldType.REFERENCE
                    && mapping.requiresMissingFieldTraversal(commonSchema.getReferencedType(i))) {
                requiresTraversalForMissingFields = true;
                break;
            }
        }

        this.requiresTraversalForMissingFields = requiresTraversalForMissingFields;
    }

    private int[] buildCommonSchemaFieldMapping(HollowObjectTypeReadState state) {
        int[] commonFieldMapping = new int[commonSchema.numFields()];
        for(int i = 0; i < commonFieldMapping.length; i++) {
            String fieldName = commonSchema.getFieldName(i);
            commonFieldMapping[i] = state.getSchema().getPosition(fieldName);
        }
        return commonFieldMapping;
    }

    public boolean requiresTraversalForMissingFields() {
        return requiresTraversalForMissingFields;
    }

    protected int fromRecordHashCode(int ordinal) {
        return recordHashCode(fromState(), ordinal, fromSchemaCommonFieldMapping, true);
    }

    protected int toRecordHashCode(int ordinal) {
        return recordHashCode(toState(), ordinal, toSchemaCommonFieldMapping, false);
    }

    private int recordHashCode(HollowObjectTypeReadState typeState, int ordinal, int[] commonSchemaFieldMapping, boolean fromState) {
        int hashCode = 0;
        for(int i = 0; i < commonSchemaFieldMapping.length; i++) {
            int typeStateFieldIndex = commonSchemaFieldMapping[i];
            if(commonSchema.getFieldType(i) == FieldType.REFERENCE) {
                int referencedOrdinal = typeState.readOrdinal(ordinal, typeStateFieldIndex);

                int ordinalIdentity = fromState ?
                        commonReferenceFieldEqualOrdinalMaps[i].getIdentityFromOrdinal(referencedOrdinal)
                        : commonReferenceFieldEqualOrdinalMaps[i].getIdentityToOrdinal(referencedOrdinal);

                if(ordinalIdentity == -1 && referencedOrdinal != -1)
                    return -1;

                hashCode = hashCode * 31 ^ HashCodes.hashInt(ordinalIdentity);
            } else {
                hashCode = hashCode * 31 ^ HashCodes.hashInt(HollowReadFieldUtils.fieldHashCode(typeState, ordinal, typeStateFieldIndex));
            }
        }
        return hashCode;
    }

    public EqualityDeterminer getEqualityDeterminer() {
        return new EqualityDeterminer() {
            public boolean recordsAreEqual(int fromOrdinal, int toOrdinal) {
                for(int i = 0; i < fromSchemaCommonFieldMapping.length; i++) {
                    if(commonSchema.getFieldType(i) == FieldType.REFERENCE) {
                        int fromReferenceOrdinal = fromState().readOrdinal(fromOrdinal, fromSchemaCommonFieldMapping[i]);
                        int toReferenceOrdinal = toState().readOrdinal(toOrdinal, toSchemaCommonFieldMapping[i]);
                        int fromIdentityOrdinal = commonReferenceFieldEqualOrdinalMaps[i].getIdentityFromOrdinal(fromReferenceOrdinal);
                        int toIdentityOrdinal = commonReferenceFieldEqualOrdinalMaps[i].getIdentityToOrdinal(toReferenceOrdinal);

                        if((fromIdentityOrdinal == -1 && fromReferenceOrdinal != -1)
                                || (toIdentityOrdinal == -1 && toReferenceOrdinal != -1)
                                || (fromIdentityOrdinal != toIdentityOrdinal))
                            return false;
                    } else if(!HollowReadFieldUtils.fieldsAreEqual(fromState(), fromOrdinal, fromSchemaCommonFieldMapping[i], toState(), toOrdinal, toSchemaCommonFieldMapping[i])) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private HollowObjectTypeReadState fromState() {
        return (HollowObjectTypeReadState) fromState;
    }

    private HollowObjectTypeReadState toState() {
        return (HollowObjectTypeReadState) toState;
    }

}
