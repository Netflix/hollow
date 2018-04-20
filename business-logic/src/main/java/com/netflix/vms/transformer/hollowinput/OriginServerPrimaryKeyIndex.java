package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class OriginServerPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, OriginServerHollow> {

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("OriginServer")).getPrimaryKey().getFieldPaths());
    }

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "OriginServer", isListenToDataRefresh, fieldPaths);
    }

    public OriginServerHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getOriginServerHollow(ordinal);
    }

}