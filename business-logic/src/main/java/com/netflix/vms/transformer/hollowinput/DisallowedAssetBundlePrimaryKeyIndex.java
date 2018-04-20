package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DisallowedAssetBundlePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DisallowedAssetBundleHollow> {

    public DisallowedAssetBundlePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public DisallowedAssetBundlePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DisallowedAssetBundle")).getPrimaryKey().getFieldPaths());
    }

    public DisallowedAssetBundlePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DisallowedAssetBundlePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DisallowedAssetBundle", isListenToDataRefresh, fieldPaths);
    }

    public DisallowedAssetBundleHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDisallowedAssetBundleHollow(ordinal);
    }

}