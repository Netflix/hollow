package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class VMSAwardDataAccessor extends AbstractHollowDataAccessor<VMSAwardHollow> {

    public static final String TYPE = "VMSAwardHollow";
    private VMSHollowInputAPI api;

    public VMSAwardDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public VMSAwardDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public VMSAwardDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public VMSAwardDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public VMSAwardHollow getRecord(int ordinal){
        return api.getVMSAwardHollow(ordinal);
    }

}