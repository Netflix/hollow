package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RightsContractPackageDataAccessor extends AbstractHollowDataAccessor<RightsContractPackageHollow> {

    public static final String TYPE = "RightsContractPackageHollow";
    private VMSHollowInputAPI api;

    public RightsContractPackageDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RightsContractPackageDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RightsContractPackageDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RightsContractPackageDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RightsContractPackageHollow getRecord(int ordinal){
        return api.getRightsContractPackageHollow(ordinal);
    }

}