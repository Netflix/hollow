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
package com.netflix.hollow.core.util;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import java.util.BitSet;

/**
 * A utility to iterate over the ordinals which were removed during the last delta transition applied to a {@link HollowReadStateEngine}. 
 */
public class RemovedOrdinalIterator {
    private final BitSet previousOrdinals;
    private final BitSet populatedOrdinals;
    private final int previousOrdinalsLength;
    private int ordinal = -1;

    public RemovedOrdinalIterator(PopulatedOrdinalListener listener) {
        this(listener.getPreviousOrdinals(), listener.getPopulatedOrdinals());
    }

    public RemovedOrdinalIterator(BitSet previousOrdinals, BitSet populatedOrdinals) {
        this.previousOrdinals = previousOrdinals;
        this.populatedOrdinals = populatedOrdinals;
        this.previousOrdinalsLength = previousOrdinals.length();
    }

    public int next() {
        while(ordinal < previousOrdinalsLength) {
            ordinal = populatedOrdinals.nextClearBit(ordinal + 1);
            if(previousOrdinals.get(ordinal))
                return ordinal;
        }

        return -1;
    }

    public void reset() {
        ordinal = -1;
    }

    public int countTotal() {
        int bookmark = ordinal;

        reset();

        int count = 0;
        while(next() != -1)
            count++;

        ordinal = bookmark;

        return count;
    }

}
