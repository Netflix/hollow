package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AwardsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, AwardsHollow> {

    public AwardsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public AwardsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Awards")).getPrimaryKey().getFieldPaths());
    }

    public AwardsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AwardsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "Awards", isListenToDataRefreah, fieldPaths);
    }

    public AwardsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAwardsHollow(ordinal);
    }

}