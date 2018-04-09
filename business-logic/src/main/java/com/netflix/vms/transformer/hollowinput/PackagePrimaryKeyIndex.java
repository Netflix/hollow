package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PackagePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, PackageHollow> {

    public PackagePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public PackagePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Package")).getPrimaryKey().getFieldPaths());
    }

    public PackagePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PackagePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Package", isListenToDataRefresh, fieldPaths);
    }

    public PackageHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPackageHollow(ordinal);
    }

}