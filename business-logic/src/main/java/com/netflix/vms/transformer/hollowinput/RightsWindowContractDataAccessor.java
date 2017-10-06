package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class RightsWindowContractDataAccessor extends AbstractHollowDataAccessor<RightsWindowContractHollow> {

    public static final String TYPE = "RightsWindowContractHollow";
    private VMSHollowInputAPI api;

    public RightsWindowContractDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public RightsWindowContractDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public RightsWindowContractDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public RightsWindowContractDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public RightsWindowContractHollow getRecord(int ordinal){
        return api.getRightsWindowContractHollow(ordinal);
    }

}