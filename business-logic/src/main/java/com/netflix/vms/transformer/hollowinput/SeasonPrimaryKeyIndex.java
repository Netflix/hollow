package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class SeasonPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, SeasonHollow> {

    public SeasonPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public SeasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Season")).getPrimaryKey().getFieldPaths());
    }

    public SeasonPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SeasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Season", isListenToDataRefresh, fieldPaths);
    }

    public SeasonHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSeasonHollow(ordinal);
    }

}