package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AssetMetaDatasPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, AssetMetaDatasHollow> {

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("AssetMetaDatas")).getPrimaryKey().getFieldPaths());
    }

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "AssetMetaDatas", isListenToDataRefresh, fieldPaths);
    }

    public AssetMetaDatasHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAssetMetaDatasHollow(ordinal);
    }

}