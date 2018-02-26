package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CacheDeploymentIntentPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CacheDeploymentIntentHollow> {

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CacheDeploymentIntent")).getPrimaryKey().getFieldPaths());
    }

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "CacheDeploymentIntent", isListenToDataRefreah, fieldPaths);
    }

    public CacheDeploymentIntentHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCacheDeploymentIntentHollow(ordinal);
    }

}