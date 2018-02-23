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

public class ByteDelegateLookupImpl extends HollowObjectAbstractDelegate implements ByteDelegate {

    private final ByteTypeAPI typeAPI;

    public ByteDelegateLookupImpl(ByteTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    @Override
    public byte getValue(int ordinal) {
        return (byte) typeAPI.getValue(ordinal);
    }

    @Override
    public Byte getValueBoxed(int ordinal) {
        Integer v = typeAPI.getValueBoxed(ordinal); // bytes are stored as ints
        return v == null ? null : v.byteValue();
    }

    @Override
    public ByteTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }
}
