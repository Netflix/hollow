/*
 *
 *  Copyright 2016 Netflix, Inc.
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
package com.netflix.hollow.api.objects.provider;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;

/**
 * A HollowObjectFactoryProvider recreates Hollow objects each time they are called for.
 */
public class HollowObjectFactoryProvider<T> extends HollowObjectProvider<T> {

    private final HollowTypeDataAccess dataAccess;
    private final HollowTypeAPI typeAPI;
    private final HollowFactory<T> factory;

    public HollowObjectFactoryProvider(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, HollowFactory<T> factory) {
        this.dataAccess = dataAccess;
        this.typeAPI = typeAPI;
        this.factory = factory;
    }

    @Override
    public T getHollowObject(int ordinal) {
        return factory.newHollowObject(dataAccess, typeAPI, ordinal);
    }

}
