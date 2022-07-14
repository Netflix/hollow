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
package com.netflix.hollow.core.util;

import static com.netflix.hollow.core.HollowConstants.ORDINAL_NONE;

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
    private int ordinal = ORDINAL_NONE;

    public RemovedOrdinalIterator(PopulatedOrdinalListener listener) {
        this(listener.getPreviousOrdinals(), listener.getPopulatedOrdinals());
    }

    public RemovedOrdinalIterator(BitSet previousOrdinals, BitSet populatedOrdinals) {
        this(previousOrdinals, populatedOrdinals, false);
    }

    public RemovedOrdinalIterator(PopulatedOrdinalListener listener, boolean flip) {
        this(listener.getPreviousOrdinals(), listener.getPopulatedOrdinals(), flip);
    }

    public RemovedOrdinalIterator(BitSet previousOrdinals, BitSet populatedOrdinals, boolean flip) {
        if(!flip) {
            this.previousOrdinals = previousOrdinals;
            this.populatedOrdinals = populatedOrdinals;
            this.previousOrdinalsLength = previousOrdinals.length();
        } else {
            this.previousOrdinals = populatedOrdinals;
            this.populatedOrdinals = previousOrdinals;
            this.previousOrdinalsLength = populatedOrdinals.length();
        }
    }

    public int next() {
        while(ordinal < previousOrdinalsLength) {
            ordinal = populatedOrdinals.nextClearBit(ordinal + 1);
            if(previousOrdinals.get(ordinal))
                return ordinal;
        }

        return ORDINAL_NONE;
    }

    public void reset() {
        ordinal = ORDINAL_NONE;
    }

    public int countTotal() {
        int bookmark = ordinal;

        reset();

        int count = 0;
        while(next() != ORDINAL_NONE)
            count++;

        ordinal = bookmark;

        return count;
    }

}
