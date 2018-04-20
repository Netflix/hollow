package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PackageStreamPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PackageStreamHollow> {

    public PackageStreamPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PackageStreamPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PackageStream")).getPrimaryKey().getFieldPaths());
    }

    public PackageStreamPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PackageStreamPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PackageStream", isListenToDataRefresh, fieldPaths);
    }

    public PackageStreamHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPackageStreamHollow(ordinal);
    }

}