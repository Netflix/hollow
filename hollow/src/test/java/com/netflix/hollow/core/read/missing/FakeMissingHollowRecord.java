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
package com.netflix.hollow.core.read.missing;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowRecordDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.schema.HollowSchema;

public class FakeMissingHollowRecord implements HollowRecord {

    private final HollowTypeDataAccess dataAccess;
    private final int ordinal;

    public FakeMissingHollowRecord(HollowTypeDataAccess dataAccess, int ordinal) {
        this.dataAccess = dataAccess;
        this.ordinal = ordinal;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public HollowSchema getSchema() {
        return dataAccess.getSchema();
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowRecordDelegate getDelegate() {
        throw new UnsupportedOperationException();
    }
}
