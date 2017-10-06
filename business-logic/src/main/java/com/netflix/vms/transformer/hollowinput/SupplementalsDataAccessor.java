package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class SupplementalsDataAccessor extends AbstractHollowDataAccessor<SupplementalsHollow> {

    public static final String TYPE = "SupplementalsHollow";
    private VMSHollowInputAPI api;

    public SupplementalsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public SupplementalsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public SupplementalsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public SupplementalsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public SupplementalsHollow getRecord(int ordinal){
        return api.getSupplementalsHollow(ordinal);
    }

}