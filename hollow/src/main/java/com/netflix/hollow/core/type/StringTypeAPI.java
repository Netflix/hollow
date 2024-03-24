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
package com.netflix.hollow.core.type;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.type.delegate.StringDelegate;

public class StringTypeAPI extends HollowObjectTypeAPI implements StringDelegate {

    public StringTypeAPI(HollowAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
    }

    public String getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleString("String", ordinal, "value");
        boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
        return getTypeDataAccess().readString(ordinal, fieldIndex[0]);
    }

    public boolean isValueEqual(int ordinal, String testValue) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleStringEquals("String", ordinal, "value", testValue);
        return getTypeDataAccess().isStringFieldEqual(ordinal, fieldIndex[0], testValue);
    }

    @Override
    public StringTypeAPI getTypeAPI() {
        return this;
    }
}