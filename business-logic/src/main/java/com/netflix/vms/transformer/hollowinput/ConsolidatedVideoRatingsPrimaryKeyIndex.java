package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedVideoRatingsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ConsolidatedVideoRatingsHollow> {

    public ConsolidatedVideoRatingsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ConsolidatedVideoRatingsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ConsolidatedVideoRatings")).getPrimaryKey().getFieldPaths());
    }

    public ConsolidatedVideoRatingsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ConsolidatedVideoRatingsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ConsolidatedVideoRatings", isListenToDataRefresh, fieldPaths);
    }

    public ConsolidatedVideoRatingsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getConsolidatedVideoRatingsHollow(ordinal);
    }

}