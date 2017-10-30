package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsAssetSetIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RightsAssetSetIdHollow> {

    public RightsAssetSetIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RightsAssetSetId")).getPrimaryKey().getFieldPaths());
    }

    public RightsAssetSetIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsAssetSetIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "RightsAssetSetId", isListenToDataRefreah, fieldPaths);
    }

    public RightsAssetSetIdHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsAssetSetIdHollow(ordinal);
    }

}