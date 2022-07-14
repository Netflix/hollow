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
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;

/**
 * This is the extension of the {@link HollowRecordDelegate} interface for cached SET type records.
 * 
 * @see HollowRecordDelegate
 */
public class HollowSetCachedDelegate<T> implements HollowSetDelegate<T>, HollowCachedDelegate {

    private final int ordinals[];
    private final int size;
    private final int hashMask;
    protected HollowSetTypeAPI typeAPI;
    private HollowSetTypeDataAccess dataAccess;

    public HollowSetCachedDelegate(HollowSetTypeDataAccess dataAccess, int ordinal) {
        this(dataAccess, null, ordinal);
    }

    public HollowSetCachedDelegate(HollowSetTypeAPI typeAPI, int ordinal) {
        this(typeAPI.getTypeDataAccess(), typeAPI, ordinal);
    }

    private HollowSetCachedDelegate(HollowSetTypeDataAccess dataAccess, HollowSetTypeAPI typeAPI, int ordinal) {
        int size = dataAccess.size(ordinal);

        int ordinals[] = new int[HashCodes.hashTableSize(size)];

        for(int i = 0; i < ordinals.length; i++) {
            ordinals[i] = dataAccess.relativeBucketValue(ordinal, i);
        }

        this.ordinals = ordinals;
        this.size = size;
        this.hashMask = ordinals.length - 1;
        this.dataAccess = dataAccess;
        this.typeAPI = typeAPI;
    }

    @Override
    public int size(int ordinal) {
        return size;
    }

    @Override
    public boolean contains(HollowSet<T> set, int ordinal, Object o) {
        if(getSchema().getHashKey() != null) {
            for(int i = 0; i < ordinals.length; i++) {
                if(ordinals[i] != -1 && set.equalsElement(ordinals[i], o))
                    return true;
            }
        } else {
            int hashCode = dataAccess.getDataAccess().getHashCodeFinder().hashCode(o);

            int bucket = HashCodes.hashInt(hashCode) & hashMask;

            while(ordinals[bucket] != -1) {
                if(set.equalsElement(ordinals[bucket], o))
                    return true;
                bucket++;
                bucket &= hashMask;
            }
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
        return new HollowOrdinalIterator() {
            private int bucket = -1;

            @Override
            public int next() {
                do {
                    bucket++;
                } while(bucket < ordinals.length && ordinals[bucket] == -1);

                if(bucket >= ordinals.length)
                    return NO_MORE_ORDINALS;
                return ordinals[bucket];
            }
        };
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

    @Override
    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (HollowSetTypeAPI) typeAPI;
        this.dataAccess = this.typeAPI.getTypeDataAccess();
    }

}
