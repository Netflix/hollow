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
package com.netflix.hollow.api.objects.generic;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

/**
 * This is a generic handle to an OBJECT type record. 
 * 
 * The Generic Hollow Object API can be used to programmatically inspect a dataset (referenced by a {@link HollowDataAccess})
 * without a custom-generated API. 
 */
public class GenericHollowObject extends HollowObject {

    public GenericHollowObject(HollowDataAccess dataAccess, String typeName, int ordinal) {
        this((HollowObjectTypeDataAccess) dataAccess.getTypeDataAccess(typeName, ordinal), ordinal);
    }

    public GenericHollowObject(HollowObjectTypeDataAccess dataAccess, int ordinal) {
        this(new HollowObjectGenericDelegate(dataAccess), ordinal);
    }

    public GenericHollowObject(HollowObjectDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public GenericHollowObject getObject(String fieldName) {
        return (GenericHollowObject) getReferencedGenericRecord(fieldName);
    }

    public GenericHollowList getList(String fieldName) {
        return (GenericHollowList) getReferencedGenericRecord(fieldName);
    }

    public GenericHollowSet getSet(String fieldName) {
        return (GenericHollowSet) getReferencedGenericRecord(fieldName);
    }

    public GenericHollowMap getMap(String fieldName) {
        return (GenericHollowMap) getReferencedGenericRecord(fieldName);
    }

    public final HollowRecord getReferencedGenericRecord(String fieldName) {
        String referencedType = getSchema().getReferencedType(fieldName);

        if(referencedType == null) {
            try {
                HollowObjectSchema hollowObjectSchema = (HollowObjectSchema) getTypeDataAccess().getDataAccess().getMissingDataHandler().handleSchema(getSchema().getName());
                referencedType = hollowObjectSchema.getReferencedType(fieldName);
                if(referencedType == null)
                    return null;
            } catch (Exception e) {
                return null;
            }
        }

        int ordinal = getOrdinal(fieldName);
        if(ordinal == -1)
            return null;

        return GenericHollowRecordHelper.instantiate(getTypeDataAccess().getDataAccess(), referencedType, ordinal);
    }

    @Override
    public String toString() {
        return new HollowRecordStringifier().stringify(this);
    }

}