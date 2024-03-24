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
import com.netflix.hollow.core.type.delegate.BooleanDelegate;

public class BooleanTypeAPI extends HollowObjectTypeAPI implements BooleanDelegate {

    public BooleanTypeAPI(HollowAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
    }

    public boolean getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Boolean", ordinal, "value") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]) == Boolean.TRUE;
    }

    public Boolean getValueBoxed(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleBoolean("Boolean", ordinal, "value");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[0]);
    }

    @Override
    public BooleanTypeAPI getTypeAPI() {
        return this;
    }
}