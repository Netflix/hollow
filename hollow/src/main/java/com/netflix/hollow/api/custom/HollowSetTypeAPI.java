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
package com.netflix.hollow.api.custom;

import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;

/**
 * This is the Hollow Type API interface for SET type records. 
 * 
 * @see HollowTypeAPI
 */
public class HollowSetTypeAPI extends HollowTypeAPI {

    public HollowSetTypeAPI(HollowAPI api, HollowSetTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess);
    }

    public int size(int ordinal) {
        return getTypeDataAccess().size(ordinal);
    }

    public boolean contains(int ordinal, int value) {
        return getTypeDataAccess().contains(ordinal, value);
    }

    public boolean contains(int ordinal, int value, int hashCode) {
        return getTypeDataAccess().contains(ordinal, value, hashCode);
    }

    public int findElement(int ordinal, Object... hashKey) {
        return getTypeDataAccess().findElement(ordinal, hashKey);
    }

    public HollowOrdinalIterator potentialMatchOrdinalIterator(int ordinal, int hashCode) {
        return getTypeDataAccess().potentialMatchOrdinalIterator(ordinal, hashCode);
    }

    public HollowOrdinalIterator getOrdinalIterator(int ordinal) {
        return getTypeDataAccess().ordinalIterator(ordinal);
    }

    @Override
    public HollowSetTypeDataAccess getTypeDataAccess() {
        return (HollowSetTypeDataAccess) typeDataAccess;
    }

}
