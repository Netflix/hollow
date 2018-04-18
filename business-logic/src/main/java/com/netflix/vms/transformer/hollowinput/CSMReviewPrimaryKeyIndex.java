package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CSMReviewPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CSMReviewHollow> {

    public CSMReviewPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CSMReviewPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CSMReview")).getPrimaryKey().getFieldPaths());
    }

    public CSMReviewPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CSMReviewPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CSMReview", isListenToDataRefresh, fieldPaths);
    }

    public CSMReviewHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCSMReviewHollow(ordinal);
    }

}