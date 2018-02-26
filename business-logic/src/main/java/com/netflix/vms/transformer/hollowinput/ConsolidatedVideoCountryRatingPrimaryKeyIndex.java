package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedVideoCountryRatingPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ConsolidatedVideoCountryRatingHollow> {

    public ConsolidatedVideoCountryRatingPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ConsolidatedVideoCountryRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ConsolidatedVideoCountryRating")).getPrimaryKey().getFieldPaths());
    }

    public ConsolidatedVideoCountryRatingPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ConsolidatedVideoCountryRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ConsolidatedVideoCountryRating", isListenToDataRefreah, fieldPaths);
    }

    public ConsolidatedVideoCountryRatingHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getConsolidatedVideoCountryRatingHollow(ordinal);
    }

}