package com.netflix.hollow.core.read.engine;

public interface HollowTypeReshardingStrategy {

    HollowTypeDataElementsSplitter createDataElementsSplitter(HollowTypeDataElements from, int shardingFactor);

    HollowTypeDataElementsJoiner createDataElementsJoiner(HollowTypeDataElements[] from);
}
