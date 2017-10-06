package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DashStreamHeaderDataDataAccessor extends AbstractHollowDataAccessor<DashStreamHeaderDataHollow> {

    public static final String TYPE = "DashStreamHeaderDataHollow";
    private VMSHollowInputAPI api;

    public DashStreamHeaderDataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DashStreamHeaderDataDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DashStreamHeaderDataDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DashStreamHeaderDataDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DashStreamHeaderDataHollow getRecord(int ordinal){
        return api.getDashStreamHeaderDataHollow(ordinal);
    }

}