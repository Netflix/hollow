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

import com.netflix.hollow.api.custom.HollowSetTypeAPI;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for lookup LIST type records.
 * 
 * @see HollowRecordDelegate
 */
public class HollowSetLookupDelegate<T> implements HollowSetDelegate<T> {

    private final HollowSetTypeDataAccess dataAccess;
    protected final HollowSetTypeAPI typeAPI;

    public HollowSetLookupDelegate(HollowSetTypeDataAccess dataAccess) {
        this(dataAccess, null);
    }

    public HollowSetLookupDelegate(HollowSetTypeAPI typeAPI) {
        this(typeAPI.getTypeDataAccess(), typeAPI);
    }

    private HollowSetLookupDelegate(HollowSetTypeDataAccess dataAccess, HollowSetTypeAPI typeAPI) {
        this.dataAccess = dataAccess;
        this.typeAPI = typeAPI;
    }

    @Override
    public int size(int ordinal) {
        return dataAccess.size(ordinal);
    }

    @Override
    public boolean contains(HollowSet<T> set, int ordinal, Object o) {
        HollowOrdinalIterator iter;

        if(getSchema().getHashKey() != null) {
            iter = dataAccess.ordinalIterator(ordinal);
        } else {
            int hashCode = dataAccess.getDataAccess().getHashCodeFinder().hashCode(o);
            iter = dataAccess.potentialMatchOrdinalIterator(ordinal, hashCode);
        }

        int potentialOrdinal = iter.next();
        while(potentialOrdinal != HollowOrdinalIterator.NO_MORE_ORDINALS) {
            if(set.equalsElement(potentialOrdinal, o))
                return true;
            potentialOrdinal = iter.next();
        }
        return false;
    }

    @Override
    public T findElement(HollowSet<T> set, int ordinal, Object... keys) {
        int elementOrdinal = dataAccess.findElement(ordinal, keys);
        if(elementOrdinal != -1)
            return set.instantiateElement(elementOrdinal);
        return null;
    }

    @Override
    public HollowOrdinalIterator iterator(int ordinal) {
        return dataAccess.ordinalIterator(ordinal);
    }

    @Override
    public HollowSetSchema getSchema() {
        return dataAccess.getSchema();
    }

    @Override
    public HollowSetTypeDataAccess getTypeDataAccess() {
        return dataAccess;
    }

    @Override
    public HollowSetTypeAPI getTypeAPI() {
        return typeAPI;
    }

}
