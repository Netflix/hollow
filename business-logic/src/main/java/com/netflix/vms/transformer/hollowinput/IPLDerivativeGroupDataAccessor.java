package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class IPLDerivativeGroupDataAccessor extends AbstractHollowDataAccessor<IPLDerivativeGroupHollow> {

    public static final String TYPE = "IPLDerivativeGroupHollow";
    private VMSHollowInputAPI api;

    public IPLDerivativeGroupDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public IPLDerivativeGroupDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public IPLDerivativeGroupDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public IPLDerivativeGroupDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public IPLDerivativeGroupHollow getRecord(int ordinal){
        return api.getIPLDerivativeGroupHollow(ordinal);
    }

}