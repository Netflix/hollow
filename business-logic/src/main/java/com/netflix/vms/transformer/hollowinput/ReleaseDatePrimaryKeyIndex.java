package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ReleaseDatePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ReleaseDateHollow> {

    public ReleaseDatePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ReleaseDate")).getPrimaryKey().getFieldPaths());
    }

    public ReleaseDatePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ReleaseDatePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "ReleaseDate", isListenToDataRefreah, fieldPaths);
    }

    public ReleaseDateHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getReleaseDateHollow(ordinal);
    }

}