package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class LocaleTerritoryCodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, LocaleTerritoryCodeHollow> {

    public LocaleTerritoryCodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public LocaleTerritoryCodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("LocaleTerritoryCode")).getPrimaryKey().getFieldPaths());
    }

    public LocaleTerritoryCodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public LocaleTerritoryCodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "LocaleTerritoryCode", isListenToDataRefreah, fieldPaths);
    }

    public LocaleTerritoryCodeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getLocaleTerritoryCodeHollow(ordinal);
    }

}