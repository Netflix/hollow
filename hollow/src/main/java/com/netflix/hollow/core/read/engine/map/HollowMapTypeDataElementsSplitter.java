package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitter;

/**
 * Split a {@code HollowMapTypeDataElements} into multiple {@code HollowMapTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * {@code numSplits} must be a power of 2.
 */
public class HollowMapTypeDataElementsSplitter extends AbstractHollowTypeDataElementsSplitter<HollowMapTypeDataElements> {

    public HollowMapTypeDataElementsSplitter(HollowMapTypeDataElements from, int numSplits) {
        super(from, numSplits);
    }

    @Override
    public void init() {
        this.to = new HollowMapTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowMapTypeDataElements(from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {

    }

    @Override
    public void copyRecords() {

    }
}
