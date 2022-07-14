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
package com.netflix.hollow.api.objects.provider;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.HollowTypeStateListener;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A HollowObjectCacheProvider caches and returns Object representations (presumably {@link HollowRecord}s) of 
 * records of a specific type. 
 */
public class HollowObjectCacheProvider<T> extends HollowObjectProvider<T> implements HollowTypeStateListener {
    private static final Logger log = Logger.getLogger(HollowObjectCacheProvider.class.getName());
    private final List<T> cachedItems;

    private HollowFactory<T> factory;
    private HollowTypeAPI typeAPI;
    private HollowTypeReadState typeReadState;

    public HollowObjectCacheProvider(HollowTypeDataAccess typeDataAccess, HollowTypeAPI typeAPI, HollowFactory<T> factory) {
        this(typeDataAccess, typeAPI, factory, null);
    }

    public HollowObjectCacheProvider(HollowTypeDataAccess typeDataAccess, HollowTypeAPI typeAPI, HollowFactory<T> factory, HollowObjectCacheProvider<T> previous) {
        if(typeDataAccess != null) {
            PopulatedOrdinalListener listener = typeDataAccess.getTypeState().getListener(PopulatedOrdinalListener.class);
            BitSet populatedOrdinals = listener.getPopulatedOrdinals();
            BitSet previousOrdinals = listener.getPreviousOrdinals();

            int length = Math.max(populatedOrdinals.length(), previousOrdinals.length());
            List<T> arr = new ArrayList<T>(length);

            for(int ordinal = 0; ordinal < length; ordinal++) {
                while(ordinal >= arr.size())
                    arr.add(null);

                if(previous != null && previousOrdinals.get(ordinal) && populatedOrdinals.get(ordinal)) {
                    T cached = previous.getHollowObject(ordinal);
                    arr.set(ordinal, cached);
                    if(cached instanceof HollowRecord)
                        ((HollowCachedDelegate) ((HollowRecord) cached).getDelegate()).updateTypeAPI(typeAPI);
                } else if(populatedOrdinals.get(ordinal)) {
                    arr.set(ordinal, instantiateCachedObject(factory, typeDataAccess, typeAPI, ordinal));
                }
            }

            if(typeDataAccess instanceof HollowTypeReadState) {
                this.factory = factory;
                this.typeAPI = typeAPI;
                this.typeReadState = (HollowTypeReadState) typeDataAccess;
                this.typeReadState.addListener(this);
            }

            this.cachedItems = arr;
        } else {
            this.cachedItems = Collections.emptyList();
        }
    }

    @Override
    public T getHollowObject(int ordinal) {
        return cachedItems.get(ordinal);
    }

    public void detach() {
        cachedItems.clear();
        factory = null;
        typeAPI = null;
        typeReadState = null;
    }

    @Override
    public void addedOrdinal(int ordinal) {
        // guard against being detached (or constructed without a HollowTypeReadState)
        if(factory == null)
            return;

        for(int i = cachedItems.size(); i <= ordinal; ++i)
            cachedItems.add(null);
        cachedItems.set(ordinal, instantiateCachedObject(factory, typeReadState, typeAPI, ordinal));
    }

    private T instantiateCachedObject(HollowFactory<T> factory, HollowTypeDataAccess typeDataAccess, HollowTypeAPI typeAPI, int ordinal) {
        try {
            return factory.newCachedHollowObject(typeDataAccess, typeAPI, ordinal);
        } catch (Throwable th) {
            log.log(Level.SEVERE, "Cached object instantiation failed", th);
            return null;
        }
    }

    @Override
    public void beginUpdate() {
    }

    @Override
    public void removedOrdinal(int ordinal) {
    }

    @Override
    public void endUpdate() {
    }
}
