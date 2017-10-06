package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamDeploymentDataAccessor extends AbstractHollowDataAccessor<StreamDeploymentHollow> {

    public static final String TYPE = "StreamDeploymentHollow";
    private VMSHollowInputAPI api;

    public StreamDeploymentDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamDeploymentDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamDeploymentDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamDeploymentDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamDeploymentHollow getRecord(int ordinal){
        return api.getStreamDeploymentHollow(ordinal);
    }

}