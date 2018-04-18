package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CacheDeploymentIntentPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CacheDeploymentIntentHollow> {

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CacheDeploymentIntent")).getPrimaryKey().getFieldPaths());
    }

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CacheDeploymentIntentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CacheDeploymentIntent", isListenToDataRefresh, fieldPaths);
    }

    public CacheDeploymentIntentHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCacheDeploymentIntentHollow(ordinal);
    }

}