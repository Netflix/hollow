package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FestivalsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, FestivalsHollow> {

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Festivals")).getPrimaryKey().getFieldPaths());
    }

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Festivals", isListenToDataRefresh, fieldPaths);
    }

    public FestivalsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getFestivalsHollow(ordinal);
    }

}