package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedCertSystemRatingPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ConsolidatedCertSystemRatingHollow> {

    public ConsolidatedCertSystemRatingPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ConsolidatedCertSystemRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ConsolidatedCertSystemRating")).getPrimaryKey().getFieldPaths());
    }

    public ConsolidatedCertSystemRatingPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ConsolidatedCertSystemRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ConsolidatedCertSystemRating", isListenToDataRefreah, fieldPaths);
    }

    public ConsolidatedCertSystemRatingHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getConsolidatedCertSystemRatingHollow(ordinal);
    }

}