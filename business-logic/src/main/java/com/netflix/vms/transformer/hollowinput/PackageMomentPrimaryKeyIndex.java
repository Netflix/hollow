package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PackageMomentPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PackageMomentHollow> {

    public PackageMomentPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public PackageMomentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("PackageMoment")).getPrimaryKey().getFieldPaths());
    }

    public PackageMomentPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PackageMomentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "PackageMoment", isListenToDataRefreah, fieldPaths);
    }

    public PackageMomentHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPackageMomentHollow(ordinal);
    }

}