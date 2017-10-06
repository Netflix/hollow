package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamDeploymentInfoDataAccessor extends AbstractHollowDataAccessor<StreamDeploymentInfoHollow> {

    public static final String TYPE = "StreamDeploymentInfoHollow";
    private VMSHollowInputAPI api;

    public StreamDeploymentInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamDeploymentInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamDeploymentInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamDeploymentInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamDeploymentInfoHollow getRecord(int ordinal){
        return api.getStreamDeploymentInfoHollow(ordinal);
    }

}