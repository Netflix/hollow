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
package com.netflix.hollow.api.objects.delegate;

import com.netflix.hollow.api.custom.HollowListTypeAPI;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.schema.HollowListSchema;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for cached LIST type records.
 * 
 * @see HollowRecordDelegate
 */
public class HollowListCachedDelegate<T> implements HollowListDelegate<T>, HollowCachedDelegate {

    private final int ordinals[];
    protected HollowListTypeAPI typeAPI;
    private HollowListTypeDataAccess dataAccess;

    public HollowListCachedDelegate(HollowListTypeDataAccess dataAccess, int ordinal) {
        this(dataAccess, null, ordinal);
    }

    public HollowListCachedDelegate(HollowListTypeAPI typeAPI, int ordinal) {
        this(typeAPI.getTypeDataAccess(), typeAPI, ordinal);
    }

    private HollowListCachedDelegate(HollowListTypeDataAccess dataAccess, HollowListTypeAPI typeAPI, int ordinal) {
        int ordinals[] = new int[dataAccess.size(ordinal)];

        for(int i = 0; i < ordinals.length; i++)
            ordinals[i] = dataAccess.getElementOrdinal(ordinal, i);

        this.ordinals = ordinals;
        this.dataAccess = dataAccess;
        this.typeAPI = typeAPI;
    }

    @Override
    public int size(int ordinal) {
        return ordinals.length;
    }

    @Override
    public T get(HollowList<T> list, int ordinal, int idx) {
        return list.instantiateElement(ordinals[idx]);
    }

    @Override
    public final boolean contains(HollowList<T> list, int ordinal, Object o) {
        return indexOf(list, ordinal, o) != -1;
    }

    @Override
    public final int indexOf(HollowList<T> list, int ordinal, Object o) {
        for(int i = 0; i < ordinals.length; i++) {
            if(list.equalsElement(ordinals[i], o))
                return i;
        }
        return -1;
    }

    @Override
    public final int lastIndexOf(HollowList<T> list, int ordinal, Object o) {
        for(int i = ordinals.length - 1; i >= 0; i--) {
            if(list.equalsElement(ordinals[i], o))
                return i;
        }
        return -1;
    }

    @Override
    public HollowListSchema getSchema() {
        return dataAccess.getSchema();
    }

    @Override
    public HollowListTypeDataAccess getTypeDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowListTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (HollowListTypeAPI) typeAPI;
        this.dataAccess = this.typeAPI.getTypeDataAccess();
    }
}
