package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CdnDeploymentDataAccessor extends AbstractHollowDataAccessor<CdnDeploymentHollow> {

    public static final String TYPE = "CdnDeploymentHollow";
    private VMSHollowInputAPI api;

    public CdnDeploymentDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CdnDeploymentDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CdnDeploymentDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CdnDeploymentDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CdnDeploymentHollow getRecord(int ordinal){
        return api.getCdnDeploymentHollow(ordinal);
    }

}