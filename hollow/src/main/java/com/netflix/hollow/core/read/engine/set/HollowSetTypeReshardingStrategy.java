package com.netflix.hollow.core.read.engine.set;

import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsJoiner;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsSplitter;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;

public class HollowSetTypeReshardingStrategy implements HollowTypeReshardingStrategy {
    @Override
    public HollowTypeDataElementsSplitter createDataElementsSplitter(HollowTypeDataElements from, int shardingFactor) {
        return new HollowSetTypeDataElementsSplitter((HollowSetTypeDataElements) from, shardingFactor);
    }

    @Override
    public HollowTypeDataElementsJoiner createDataElementsJoiner(HollowTypeDataElements[] from) {
        // SNAP: TODO: array casting?
        return new HollowSetTypeDataElementsJoiner((HollowSetTypeDataElements[]) from);
    }
}
