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
import com.netflix.hollow.core.type.delegate.IntegerDelegate;

public class IntegerTypeAPI extends HollowObjectTypeAPI implements IntegerDelegate {

    private final HollowAPI api;

    public IntegerTypeAPI(HollowAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(typeDataAccess, new String[] {
            "value"
        });
        this.api = api;
    }

    public int getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleInt("Integer", ordinal, "value");
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getValueBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleInt("Integer", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if(i == Integer.MIN_VALUE)
            return null;
        return Integer.valueOf(i);
    }

    public HollowAPI getAPI() {
        return this.api;
    }

    @Override
    public IntegerTypeAPI getTypeAPI() {
        return this;
    }

}