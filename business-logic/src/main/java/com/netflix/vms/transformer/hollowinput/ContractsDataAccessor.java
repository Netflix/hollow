package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ContractsDataAccessor extends AbstractHollowDataAccessor<ContractsHollow> {

    public static final String TYPE = "ContractsHollow";
    private VMSHollowInputAPI api;

    public ContractsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ContractsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ContractsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ContractsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ContractsHollow getRecord(int ordinal){
        return api.getContractsHollow(ordinal);
    }

}