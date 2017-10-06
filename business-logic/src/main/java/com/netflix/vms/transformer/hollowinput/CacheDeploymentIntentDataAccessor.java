package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CacheDeploymentIntentDataAccessor extends AbstractHollowDataAccessor<CacheDeploymentIntentHollow> {

    public static final String TYPE = "CacheDeploymentIntentHollow";
    private VMSHollowInputAPI api;

    public CacheDeploymentIntentDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CacheDeploymentIntentDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CacheDeploymentIntentDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CacheDeploymentIntentDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CacheDeploymentIntentHollow getRecord(int ordinal){
        return api.getCacheDeploymentIntentHollow(ordinal);
    }

}