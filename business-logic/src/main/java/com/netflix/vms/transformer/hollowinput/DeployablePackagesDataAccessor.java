package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DeployablePackagesDataAccessor extends AbstractHollowDataAccessor<DeployablePackagesHollow> {

    public static final String TYPE = "DeployablePackagesHollow";
    private VMSHollowInputAPI api;

    public DeployablePackagesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DeployablePackagesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DeployablePackagesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DeployablePackagesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DeployablePackagesHollow getRecord(int ordinal){
        return api.getDeployablePackagesHollow(ordinal);
    }

}