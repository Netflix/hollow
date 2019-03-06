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

import com.netflix.hollow.api.custom.HollowObjectTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.type.delegate.LongDelegateLookupImpl;

public class LongTypeAPI extends HollowObjectTypeAPI {

    private final LongDelegateLookupImpl delegateLookupImpl;

    public LongTypeAPI(HollowObjectTypeDataAccess typeDataAccess) {
        super(typeDataAccess, new String[] {
            "value"
        });
        this.delegateLookupImpl = new LongDelegateLookupImpl(this);
    }

    public long getValue(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Long", ordinal, "value");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getValueBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Long", ordinal, "value");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }

    public LongDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

}