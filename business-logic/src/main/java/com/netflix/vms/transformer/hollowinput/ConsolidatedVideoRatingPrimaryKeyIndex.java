package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedVideoRatingPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ConsolidatedVideoRatingHollow> {

    public ConsolidatedVideoRatingPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ConsolidatedVideoRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ConsolidatedVideoRating")).getPrimaryKey().getFieldPaths());
    }

    public ConsolidatedVideoRatingPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ConsolidatedVideoRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ConsolidatedVideoRating", isListenToDataRefreah, fieldPaths);
    }

    public ConsolidatedVideoRatingHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getConsolidatedVideoRatingHollow(ordinal);
    }

}