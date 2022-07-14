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

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowListLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowMapLookupDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectGenericDelegate;
import com.netflix.hollow.api.objects.delegate.HollowSetLookupDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowMapTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowListMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowMapMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowSetMissingDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;

/**
 * Contains some useful methods for interacting with the Generic Hollow Objects API. 
 */
public class GenericHollowRecordHelper {

    public static HollowRecord instantiate(HollowDataAccess dataAccess, String typeName, int ordinal) {
        HollowTypeDataAccess typeState = dataAccess.getTypeDataAccess(typeName, ordinal);

        if(typeState != null) {
            if(typeState instanceof HollowObjectTypeDataAccess)
                return new GenericHollowObject(new HollowObjectGenericDelegate((HollowObjectTypeDataAccess) typeState), ordinal);
            if(typeState instanceof HollowListTypeDataAccess)
                return new GenericHollowList(new HollowListLookupDelegate<HollowRecord>((HollowListTypeDataAccess) typeState), ordinal);
            if(typeState instanceof HollowSetTypeDataAccess)
                return new GenericHollowSet(new HollowSetLookupDelegate<HollowRecord>((HollowSetTypeDataAccess) typeState), ordinal);
            if(typeState instanceof HollowMapTypeDataAccess)
                return new GenericHollowMap(new HollowMapLookupDelegate<HollowRecord, HollowRecord>((HollowMapTypeDataAccess) typeState), ordinal);
        } else {
            HollowSchema schema = dataAccess.getMissingDataHandler().handleSchema(typeName);

            if(schema instanceof HollowObjectSchema)
                return new GenericHollowObject(new HollowObjectGenericDelegate(new HollowObjectMissingDataAccess(dataAccess, typeName)), ordinal);
            if(schema instanceof HollowListSchema)
                return new GenericHollowList(new HollowListLookupDelegate<HollowRecord>(new HollowListMissingDataAccess(dataAccess, typeName)), ordinal);
            if(schema instanceof HollowSetSchema)
                return new GenericHollowSet(new HollowSetLookupDelegate<HollowRecord>(new HollowSetMissingDataAccess(dataAccess, typeName)), ordinal);
            if(schema instanceof HollowMapSchema)
                return new GenericHollowMap(new HollowMapLookupDelegate<HollowRecord, HollowRecord>(new HollowMapMissingDataAccess(dataAccess, typeName)), ordinal);
        }

        throw new UnsupportedOperationException("I don't know how to instantiate a generic object given a " + typeState.getClass().getSimpleName());
    }

    public static boolean equalObject(String typeName, int ordinal, Object testObject) {
        if(testObject instanceof HollowRecord) {
            HollowRecord testRec = (HollowRecord) testObject;
            if(testRec.getOrdinal() == ordinal) {
                String otherType = testRec.getSchema().getName();

                return otherType.equals(typeName);
            }
        }
        return false;
    }
}
