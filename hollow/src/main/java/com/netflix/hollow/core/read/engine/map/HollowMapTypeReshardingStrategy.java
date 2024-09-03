package com.netflix.hollow.core.read.engine.map;

import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsJoiner;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsSplitter;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;

public class HollowMapTypeReshardingStrategy implements HollowTypeReshardingStrategy {
    @Override
    public HollowTypeDataElementsSplitter createDataElementsSplitter(HollowTypeDataElements from, int shardingFactor) {
        return new HollowMapTypeDataElementsSplitter((HollowMapTypeDataElements) from, shardingFactor);
    }

    @Override
    public HollowTypeDataElementsJoiner createDataElementsJoiner(HollowTypeDataElements[] from) {
        // SNAP: TODO: array casting?
        return new HollowMapTypeDataElementsJoiner((HollowMapTypeDataElements[]) from);
    }
}
