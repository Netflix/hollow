package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class OriginServerPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, OriginServerHollow> {

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("OriginServer")).getPrimaryKey().getFieldPaths());
    }

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public OriginServerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "OriginServer", isListenToDataRefreah, fieldPaths);
    }

    public OriginServerHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getOriginServerHollow(ordinal);
    }

}