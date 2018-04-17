package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AwardsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, AwardsHollow> {

    public AwardsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public AwardsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Awards")).getPrimaryKey().getFieldPaths());
    }

    public AwardsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AwardsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Awards", isListenToDataRefresh, fieldPaths);
    }

    public AwardsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAwardsHollow(ordinal);
    }

}