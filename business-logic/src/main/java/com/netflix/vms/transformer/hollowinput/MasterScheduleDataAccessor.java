package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class MasterScheduleDataAccessor extends AbstractHollowDataAccessor<MasterScheduleHollow> {

    public static final String TYPE = "MasterScheduleHollow";
    private VMSHollowInputAPI api;

    public MasterScheduleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public MasterScheduleDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public MasterScheduleDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public MasterScheduleDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public MasterScheduleHollow getRecord(int ordinal){
        return api.getMasterScheduleHollow(ordinal);
    }

}