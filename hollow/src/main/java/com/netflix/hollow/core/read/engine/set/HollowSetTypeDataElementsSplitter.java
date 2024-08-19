package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitter;

/**
 * Split a {@code HollowSetTypeDataElements} into multiple {@code HollowSetTypeDataElements}s.
 * Ordinals are remapped and corresponding data is copied over.
 * The original data elements are not destroyed.
 * {@code numSplits} must be a power of 2.
 */
public class HollowSetTypeDataElementsSplitter extends AbstractHollowTypeDataElementsSplitter {

    public HollowSetTypeDataElementsSplitter(HollowSetTypeDataElements from, int numSplits) {
        super(from, numSplits);
    }

    @Override
    public void init() {
        this.to = new HollowSetTypeDataElements[numSplits];
        for(int i=0;i<to.length;i++) {
            to[i] = new HollowSetTypeDataElements(from.memoryMode, from.memoryRecycler);
        }
    }

    @Override
    public void populateStats() {

    }

    @Override
    public void copyRecords() {

    }
}
