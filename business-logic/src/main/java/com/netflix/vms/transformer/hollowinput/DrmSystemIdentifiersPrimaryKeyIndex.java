package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DrmSystemIdentifiersPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DrmSystemIdentifiersHollow> {

    public DrmSystemIdentifiersPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public DrmSystemIdentifiersPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DrmSystemIdentifiers")).getPrimaryKey().getFieldPaths());
    }

    public DrmSystemIdentifiersPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DrmSystemIdentifiersPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DrmSystemIdentifiers", isListenToDataRefresh, fieldPaths);
    }

    public DrmSystemIdentifiersHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDrmSystemIdentifiersHollow(ordinal);
    }

}