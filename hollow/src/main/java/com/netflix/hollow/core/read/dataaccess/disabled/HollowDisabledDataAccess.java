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
package com.netflix.hollow.core.read.dataaccess.disabled;

import com.netflix.hollow.api.client.StaleHollowReferenceDetector;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowProxyDataAccess;
import com.netflix.hollow.core.read.missing.MissingDataHandler;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.HollowObjectHashCodeFinder;
import java.util.Collection;
import java.util.List;

/**
 * A HollowDisabledDataAccess throws an IllegalStateException when access is attempted.  This is swapped into 
 * a {@link HollowProxyDataAccess} if the {@link StaleHollowReferenceDetector} detects that stale references are
 * held but unused.
 */
public class HollowDisabledDataAccess implements HollowDataAccess {

    public static final HollowDisabledDataAccess INSTANCE = new HollowDisabledDataAccess();

    private HollowDisabledDataAccess() {
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowTypeDataAccess getTypeDataAccess(String typeName, int ordinal) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public Collection<String> getAllTypes() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowObjectHashCodeFinder getHashCodeFinder() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public MissingDataHandler getMissingDataHandler() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public List<HollowSchema> getSchemas() {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowSchema getSchema(String name) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public HollowSchema getNonNullSchema(String name) {
        throw new IllegalStateException("Data Access is Disabled");
    }

    @Override
    public void resetSampling() {
    }

    @Override
    public boolean hasSampleResults() {
        return false;
    }
}
