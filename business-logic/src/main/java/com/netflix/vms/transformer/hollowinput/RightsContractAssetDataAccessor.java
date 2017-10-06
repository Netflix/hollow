package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RightsContractAssetDataAccessor extends AbstractHollowDataAccessor<RightsContractAssetHollow> {

    public static final String TYPE = "RightsContractAssetHollow";
    private VMSHollowInputAPI api;

    public RightsContractAssetDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RightsContractAssetDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RightsContractAssetDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RightsContractAssetDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RightsContractAssetHollow getRecord(int ordinal){
        return api.getRightsContractAssetHollow(ordinal);
    }

}