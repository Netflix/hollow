package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsJoiner;


/**
 * Join multiple {@code HollowSetTypeDataElements}s into 1 {@code HollowSetTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowSetTypeDataElementsJoiner extends AbstractHollowTypeDataElementsJoiner<HollowSetTypeDataElements> {

    public HollowSetTypeDataElementsJoiner(HollowSetTypeDataElements[] from) {
        super(from);
    }

    @Override
    public void init() {
        this.to = new HollowSetTypeDataElements(from[0].memoryMode, from[0].memoryRecycler);
    }

    @Override
    public void populateStats() {
    }

    @Override
    public void copyRecords() {
    }
}
