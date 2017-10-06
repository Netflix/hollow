package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StatusDataAccessor extends AbstractHollowDataAccessor<StatusHollow> {

    public static final String TYPE = "StatusHollow";
    private VMSHollowInputAPI api;

    public StatusDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StatusDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StatusDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StatusDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StatusHollow getRecord(int ordinal){
        return api.getStatusHollow(ordinal);
    }

}