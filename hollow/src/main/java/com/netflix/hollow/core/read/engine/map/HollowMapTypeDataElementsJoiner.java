package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsJoiner;


/**
 * Join multiple {@code HollowMapTypeDataElements}s into 1 {@code HollowMapTypeDataElements}.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * The no. of passed data elements must be a power of 2.
 */
class HollowMapTypeDataElementsJoiner extends AbstractHollowTypeDataElementsJoiner<HollowMapTypeDataElements> {

    public HollowMapTypeDataElementsJoiner(HollowMapTypeDataElements[] from) {
        super(from);
    }

    @Override
    public void init() {
        this.to = new HollowMapTypeDataElements(from[0].memoryMode, from[0].memoryRecycler);
    }

    @Override
    public void populateStats() {
    }

    @Override
    public void copyRecords() {
    }
}
