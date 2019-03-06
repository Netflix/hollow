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

import com.netflix.hollow.api.objects.HollowSet;
import com.netflix.hollow.api.objects.delegate.HollowSetDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowSetTypeDataAccess;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;

/**
 * This is the Hollow Type API interface for SET type records. 
 * 
 * @see HollowTypeAPI
 */
public class HollowSetTypeAPI<T> extends HollowTypeAPI implements HollowSetDelegate<T> {

    public HollowSetTypeAPI(HollowSetTypeDataAccess typeDataAccess) {
        super(typeDataAccess);
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

    @Override
    public boolean contains(HollowSet<T> set, int ordinal, Object o) {
        HollowOrdinalIterator iter;

        if(getSchema().getHashKey() != null) {
            iter = getTypeDataAccess().ordinalIterator(ordinal);
        } else {
            int hashCode = getTypeDataAccess().getDataAccess().getHashCodeFinder().hashCode(o);
            iter = getTypeDataAccess().potentialMatchOrdinalIterator(ordinal, hashCode);
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
        int elementOrdinal = getTypeDataAccess().findElement(ordinal, keys);
        if(elementOrdinal != -1)
            return set.instantiateElement(elementOrdinal);
        return null;
    }

    @Override
    public HollowOrdinalIterator iterator(int ordinal) {
        return getTypeDataAccess().ordinalIterator(ordinal);
    }

    @Override
    public HollowSetSchema getSchema() {
        return getTypeDataAccess().getSchema();
    }

    @Override
    public HollowSetTypeAPI getTypeAPI() {
        return this;
    }

}
