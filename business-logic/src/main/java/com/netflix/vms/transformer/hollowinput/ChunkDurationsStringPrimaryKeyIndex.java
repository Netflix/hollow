package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ChunkDurationsStringPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ChunkDurationsStringHollow> {

    public ChunkDurationsStringPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ChunkDurationsStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ChunkDurationsString")).getPrimaryKey().getFieldPaths());
    }

    public ChunkDurationsStringPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ChunkDurationsStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ChunkDurationsString", isListenToDataRefreah, fieldPaths);
    }

    public ChunkDurationsStringHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getChunkDurationsStringHollow(ordinal);
    }

}