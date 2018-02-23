/*
 *
 *  Copyright 2018 Netflix, Inc.
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
import com.netflix.hollow.core.type.ByteTypeAPI;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

public class ByteDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, ByteDelegate {

    private final Byte value;
    private ByteTypeAPI typeAPI;

    public ByteDelegateCachedImpl(ByteTypeAPI typeAPI, int ordinal) {
        Integer v = typeAPI.getValueBoxed(ordinal);
        this.value = v == null ? null : v.byteValue();
        this.typeAPI = typeAPI;
    }

    @Override
    public byte getValue(int ordinal) {
        if(value == null)
            return Byte.MIN_VALUE;
        return value.byteValue();
    }

    @Override
    public Byte getValueBoxed(int ordinal) {
        return value;
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
    public ByteTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (ByteTypeAPI) typeAPI;
    }
}
