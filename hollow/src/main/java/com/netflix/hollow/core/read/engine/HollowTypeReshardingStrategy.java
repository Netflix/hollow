package com.netflix.hollow.core.read.engine;

import com.netflix.hollow.core.schema.HollowSchema;

public interface HollowTypeReshardingStrategy {

    HollowTypeDataElements[] createTypeDataElements(int len);

    HollowTypeReadStateShard createTypeReadStateShard(HollowSchema schema, HollowTypeDataElements dataElements, int shardOrdinalShift);

}
