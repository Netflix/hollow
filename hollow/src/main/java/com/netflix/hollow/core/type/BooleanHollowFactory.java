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

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.type.delegate.BooleanDelegate;
import com.netflix.hollow.core.type.delegate.BooleanDelegateCachedImpl;

public class BooleanHollowFactory extends HollowFactory<HBoolean> {

    @Override
    public HBoolean newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return new HBoolean(((BooleanDelegate)typeAPI), ordinal);
    }

    @Override
    public HBoolean newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return new HBoolean(new BooleanDelegateCachedImpl((BooleanTypeAPI)typeAPI, ordinal), ordinal);
    }

}