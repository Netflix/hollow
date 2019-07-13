package com.netflix.vms.transformer.input.api.gen.award;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class VMSAwardDataAccessor extends AbstractHollowDataAccessor<VMSAward> {

    public static final String TYPE = "VMSAward";
    private AwardAPI api;

    public VMSAwardDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (AwardAPI)consumer.getAPI();
    }

    public VMSAwardDataAccessor(HollowReadStateEngine rStateEngine, AwardAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public VMSAwardDataAccessor(HollowReadStateEngine rStateEngine, AwardAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public VMSAwardDataAccessor(HollowReadStateEngine rStateEngine, AwardAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public VMSAward getRecord(int ordinal){
        return api.getVMSAward(ordinal);
    }

}