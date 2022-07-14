/*
 *  Copyright 2021 Netflix, Inc.
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
package com.netflix.hollow.api.perfapi;

import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import java.util.Arrays;
import java.util.BitSet;

public class HollowPerfAPICache<T> {
    private static final Object[] EMPTY_CACHE = new Object[0];

    private final HollowTypePerfAPI typeAPI;
    private final Object[] cachedItems;

    public HollowPerfAPICache(
            HollowTypePerfAPI typeAPI,
            POJOInstantiator<T> instantiator,
            HollowPerfAPICache<T> previous) {

        this.typeAPI = typeAPI;
        if(!typeAPI.isMissingType()) {
            PopulatedOrdinalListener listener = typeAPI.typeAccess().getTypeState()
                    .getListener(PopulatedOrdinalListener.class);
            BitSet populatedOrdinals = listener.getPopulatedOrdinals();
            BitSet previousOrdinals = listener.getPreviousOrdinals();

            int length = Math.max(populatedOrdinals.length(), previousOrdinals.length());
            // Copy over all previously cached items, resizing the array if necessary.
            // This is required if removed ordinals are queried in the cache.
            // For example, see SpecificTypeUpdateNotifier.buildFastlaneUpdateNotificationLists
            Object[] arr = previous != null
                    ? Arrays.copyOf(previous.cachedItems, length)
                    : new Object[length];

            for(int ordinal = 0; ordinal < length; ordinal++) {
                boolean previouslyPopulated = previous != null && previousOrdinals.get(ordinal);
                if(!previouslyPopulated) {
                    // If not previously populated and currently populated then create a new cached instance.
                    // Otherwise, if not previously populated and not currently populated than null out any
                    // possibly present old cached value (create a hole)
                    boolean currentlyPopulated = populatedOrdinals.get(ordinal);
                    arr[ordinal] = currentlyPopulated
                            ? instantiator.instantiate(Ref.toRefWithTypeMasked(typeAPI.maskedTypeIdx, ordinal))
                            : null;
                }
                // If previously populated then retain the cached item
            }

            this.cachedItems = arr;
        } else {
            this.cachedItems = EMPTY_CACHE;
        }
    }

    public T get(long ref) {
        @SuppressWarnings("unchecked")
        T t = (T) cachedItems[typeAPI.ordinal(ref)];
        return t;
    }

    public Object[] getCachedItems() {
        return Arrays.copyOf(cachedItems, cachedItems.length);
    }

}
