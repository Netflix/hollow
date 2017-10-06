package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DerivativeTagDataAccessor extends AbstractHollowDataAccessor<DerivativeTagHollow> {

    public static final String TYPE = "DerivativeTagHollow";
    private VMSHollowInputAPI api;

    public DerivativeTagDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DerivativeTagDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DerivativeTagDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DerivativeTagDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DerivativeTagHollow getRecord(int ordinal){
        return api.getDerivativeTagHollow(ordinal);
    }

}