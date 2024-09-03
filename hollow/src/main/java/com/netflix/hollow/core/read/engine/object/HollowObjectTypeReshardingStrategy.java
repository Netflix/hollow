package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.read.engine.HollowTypeDataElements;
import com.netflix.hollow.core.read.engine.HollowTypeReadStateShard;
import com.netflix.hollow.core.read.engine.HollowTypeReshardingStrategy;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;

public class HollowObjectTypeReshardingStrategy implements HollowTypeReshardingStrategy {
    @Override
    public HollowTypeDataElements[] createTypeDataElements(int len) {
        return new HollowObjectTypeDataElements[len];
    }

    @Override
    public HollowTypeReadStateShard createTypeReadStateShard(HollowSchema schema, HollowTypeDataElements dataElements, int shardOrdinalShift) {
        return new HollowObjectTypeReadStateShard((HollowObjectSchema) schema, (HollowObjectTypeDataElements) dataElements, shardOrdinalShift);
    }


}
