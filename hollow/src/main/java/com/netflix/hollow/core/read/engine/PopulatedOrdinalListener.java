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
package com.netflix.hollow.core.read.engine;

import java.util.BitSet;

/**
 * A PopulatedOrdinalListener is (unless explicitly specified) automatically registered with each type
 * in a {@link HollowReadStateEngine}.  This listener tracks the populated and previous ordinals using
 * BitSets.
 */
public class PopulatedOrdinalListener implements HollowTypeStateListener {

    private final BitSet previousOrdinals;
    private final BitSet populatedOrdinals;

    public PopulatedOrdinalListener() {
        this.populatedOrdinals = new BitSet();
        this.previousOrdinals = new BitSet();
    }

    @Override
    public void beginUpdate() {
        previousOrdinals.clear();
        previousOrdinals.or(populatedOrdinals);
    }

    @Override
    public void addedOrdinal(int ordinal) {
        populatedOrdinals.set(ordinal);
    }

    @Override
    public void removedOrdinal(int ordinal) {
        populatedOrdinals.clear(ordinal);
    }

    @Override
    public void endUpdate() {
    }

    public boolean updatedLastCycle() {
        return !populatedOrdinals.equals(previousOrdinals);
    }

    public BitSet getPopulatedOrdinals() {
        return populatedOrdinals;
    }

    public BitSet getPreviousOrdinals() {
        return previousOrdinals;
    }

}
