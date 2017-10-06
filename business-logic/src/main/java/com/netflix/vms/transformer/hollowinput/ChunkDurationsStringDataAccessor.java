package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ChunkDurationsStringDataAccessor extends AbstractHollowDataAccessor<ChunkDurationsStringHollow> {

    public static final String TYPE = "ChunkDurationsStringHollow";
    private VMSHollowInputAPI api;

    public ChunkDurationsStringDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ChunkDurationsStringDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ChunkDurationsStringDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ChunkDurationsStringDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ChunkDurationsStringHollow getRecord(int ordinal){
        return api.getChunkDurationsStringHollow(ordinal);
    }

}