/*
 *
 *  Copyright 2017 Netflix, Inc.
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
package com.netflix.hollow.core.type.delegate;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.type.StringTypeAPI;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

public class StringDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, StringDelegate {

    private final String value;
    private StringTypeAPI typeAPI;

    public StringDelegateCachedImpl(StringTypeAPI typeAPI, int ordinal) {
        this.value = typeAPI.getValue(ordinal);
        this.typeAPI = typeAPI;
    }

    @Override
    public String getValue(int ordinal) {
        return value;
    }

    @Override
    public boolean isValueEqual(int ordinal, String testValue) {
        if(testValue == null)
            return value == null;
        return testValue.equals(value);
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    @Override
    public StringTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (StringTypeAPI) typeAPI;
    }

}