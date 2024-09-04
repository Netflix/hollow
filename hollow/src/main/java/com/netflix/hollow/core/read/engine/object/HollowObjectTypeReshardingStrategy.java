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

    // SNAP: TODO: can drop once base class supports resharding all types
    @Override
    public boolean shouldReshard(int currNumShards, int deltaNumShards) {
        return currNumShards!=0 && deltaNumShards!=0 && currNumShards!=deltaNumShards;
    }
}
