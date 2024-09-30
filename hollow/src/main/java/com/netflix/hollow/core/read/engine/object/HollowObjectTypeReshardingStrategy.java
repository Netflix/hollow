package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsJoiner;
import com.netflix.hollow.core.read.engine.HollowTypeDataElementsSplitter;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;

public class HollowObjectTypeReshardingStrategy extends HollowTypeReshardingStrategy {
    @Override
    public HollowTypeDataElementsSplitter createDataElementsSplitter(HollowTypeDataElements from, int shardingFactor) {
        return new HollowObjectTypeDataElementsSplitter((HollowObjectTypeDataElements) from, shardingFactor);
    }

    @Override
    public HollowTypeDataElementsJoiner createDataElementsJoiner(HollowTypeDataElements[] from) {
        return new HollowObjectTypeDataElementsJoiner((HollowObjectTypeDataElements[]) from);
    }
}
