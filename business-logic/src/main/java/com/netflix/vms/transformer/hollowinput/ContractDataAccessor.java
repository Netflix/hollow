package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ContractDataAccessor extends AbstractHollowDataAccessor<ContractHollow> {

    public static final String TYPE = "ContractHollow";
    private VMSHollowInputAPI api;

    public ContractDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ContractDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ContractDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ContractDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ContractHollow getRecord(int ordinal){
        return api.getContractHollow(ordinal);
    }

}