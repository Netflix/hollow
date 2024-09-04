package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsJoiner;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsSplitter;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;

public class HollowListTypeReshardingStrategy implements HollowTypeReshardingStrategy {
    @Override
    public HollowTypeDataElementsSplitter createDataElementsSplitter(HollowTypeDataElements from, int shardingFactor) {
        return new HollowListTypeDataElementsSplitter((HollowListTypeDataElements) from, shardingFactor);
    }

    @Override
    public HollowTypeDataElementsJoiner createDataElementsJoiner(HollowTypeDataElements[] from) {
        return new HollowListTypeDataElementsJoiner((HollowListTypeDataElements[]) from);
    }
}
