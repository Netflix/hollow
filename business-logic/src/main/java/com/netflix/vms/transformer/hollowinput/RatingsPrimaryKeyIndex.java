package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RatingsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RatingsHollow> {

    public RatingsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public RatingsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Ratings")).getPrimaryKey().getFieldPaths());
    }

    public RatingsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RatingsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Ratings", isListenToDataRefresh, fieldPaths);
    }

    public RatingsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRatingsHollow(ordinal);
    }

}