package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class AssetMetaDatasPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, AssetMetaDatasHollow> {

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("AssetMetaDatas")).getPrimaryKey().getFieldPaths());
    }

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AssetMetaDatasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "AssetMetaDatas", isListenToDataRefreah, fieldPaths);
    }

    public AssetMetaDatasHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAssetMetaDatasHollow(ordinal);
    }

}