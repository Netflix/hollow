package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsAssetPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RightsAssetHollow> {

    public RightsAssetPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RightsAsset")).getPrimaryKey().getFieldPaths());
    }

    public RightsAssetPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsAssetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "RightsAsset", isListenToDataRefreah, fieldPaths);
    }

    public RightsAssetHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsAssetHollow(ordinal);
    }

}