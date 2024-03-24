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
import com.netflix.hollow.core.type.delegate.FloatDelegate;

public class FloatTypeAPI extends HollowObjectTypeAPI implements FloatDelegate {

    public FloatTypeAPI(HollowAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
    }

    public float getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleFloat("Float", ordinal, "value");
        return getTypeDataAccess().readFloat(ordinal, fieldIndex[0]);
    }

    public Float getValueBoxed(int ordinal) {
        float f;
        if(fieldIndex[0] == -1) {
            f = missingDataHandler().handleFloat("Float", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            f = getTypeDataAccess().readFloat(ordinal, fieldIndex[0]);
        }        return Float.isNaN(f) ? null : Float.valueOf(f);
    }

    @Override
    public FloatTypeAPI getTypeAPI() {
        return this;
    }
}