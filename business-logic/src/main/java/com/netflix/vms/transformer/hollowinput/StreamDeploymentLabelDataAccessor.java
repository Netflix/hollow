package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamDeploymentLabelDataAccessor extends AbstractHollowDataAccessor<StreamDeploymentLabelHollow> {

    public static final String TYPE = "StreamDeploymentLabelHollow";
    private VMSHollowInputAPI api;

    public StreamDeploymentLabelDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamDeploymentLabelDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamDeploymentLabelDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamDeploymentLabelDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamDeploymentLabelHollow getRecord(int ordinal){
        return api.getStreamDeploymentLabelHollow(ordinal);
    }

}