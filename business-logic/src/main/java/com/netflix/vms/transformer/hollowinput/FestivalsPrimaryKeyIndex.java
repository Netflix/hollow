package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FestivalsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, FestivalsHollow> {

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Festivals")).getPrimaryKey().getFieldPaths());
    }

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public FestivalsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "Festivals", isListenToDataRefreah, fieldPaths);
    }

    public FestivalsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getFestivalsHollow(ordinal);
    }

}