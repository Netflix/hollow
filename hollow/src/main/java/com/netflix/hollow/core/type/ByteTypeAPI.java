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
package com.netflix.hollow.core.type;

import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.type.delegate.ByteDelegateLookupImpl;

public class ByteTypeAPI extends HollowObjectTypeAPI {

    private final ByteDelegateLookupImpl delegateLookupImpl;

    public ByteTypeAPI(HollowAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new ByteDelegateLookupImpl(this);
    }

    public int getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleByte("Byte", ordinal, "value");
        // bytes are actually stored as ints
        return getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
    }

    public Integer getValueBoxed(int ordinal) {
        int i;
        if(fieldIndex[0] == -1) {
            i = missingDataHandler().handleByte("Byte", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            // bytes are actually stored as ints
            i = getTypeDataAccess().readInt(ordinal, fieldIndex[0]);
        }
        if ((i & (1 << 7)) != 0) // safety check - if not a byte, return null
            return null;
        return Integer.valueOf(i);
    }

    public ByteDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }
}
