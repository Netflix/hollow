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

import com.netflix.hollow.api.objects.HollowList;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowListDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowListTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;

/**
 * This is the Hollow Type API interface for LIST type records. 
 * 
 * @see HollowTypeAPI
 */
public class HollowListTypeAPI<T extends HollowRecord> extends HollowTypeAPI implements HollowListDelegate<T> {

    protected final HollowListTypeDataAccess listTypeDataAccess;

    public HollowListTypeAPI(HollowListTypeDataAccess typeDataAccess) {
        super(typeDataAccess);
        this.listTypeDataAccess = typeDataAccess;
    }

    public int getElementOrdinal(int ordinal, int listIdx) {
        return getTypeDataAccess().getElementOrdinal(ordinal, listIdx);
    }
    
    public HollowOrdinalIterator getOrdinalIterator(int ordinal) {
        return getTypeDataAccess().ordinalIterator(ordinal);
    }

    @Override
    public HollowListTypeDataAccess getTypeDataAccess() {
        return (HollowListTypeDataAccess) typeDataAccess;
    }

    @Override
    public int size(int ordinal) {
        return getTypeDataAccess().size(ordinal);
    }

    @Override
    public T get(HollowList<T> list, int ordinal, int index) {
        int elementOrdinal = getTypeDataAccess().getElementOrdinal(ordinal, index);
        return list.instantiateElement(elementOrdinal);
    }

    @Override
    public final boolean contains(HollowList<T> list, int ordinal, Object o) {
        return indexOf(list, ordinal, o) != -1;
    }

    @Override
    public final int indexOf(HollowList<T> list, int ordinal, Object o) {
        int size = size(ordinal);
        for(int i=0;i<size;i++) {
            int elementOrdinal = getTypeDataAccess().getElementOrdinal(ordinal, i);
            if(list.equalsElement(elementOrdinal, o))
                return i;
        }
        return -1;
    }

    @Override
    public final int lastIndexOf(HollowList<T> list, int ordinal, Object o) {
        int size = size(ordinal);
        for(int i=size - 1; i>=0; i--) {
            int elementOrdinal = getTypeDataAccess().getElementOrdinal(ordinal, i);
            if(list.equalsElement(elementOrdinal, o))
                return i;
        }
        return -1;
    }

    @Override
    public HollowListSchema getSchema() {
        return getTypeDataAccess().getSchema();
    }

    @Override
    public HollowListTypeAPI getTypeAPI() {
        return this;
    }
}
