package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DrmInfoStringPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DrmInfoStringHollow> {

    public DrmInfoStringPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public DrmInfoStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DrmInfoString")).getPrimaryKey().getFieldPaths());
    }

    public DrmInfoStringPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DrmInfoStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DrmInfoString", isListenToDataRefresh, fieldPaths);
    }

    public DrmInfoStringHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDrmInfoStringHollow(ordinal);
    }

}