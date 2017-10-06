package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class AbsoluteScheduleDataAccessor extends AbstractHollowDataAccessor<AbsoluteScheduleHollow> {

    public static final String TYPE = "AbsoluteScheduleHollow";
    private VMSHollowInputAPI api;

    public AbsoluteScheduleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public AbsoluteScheduleDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public AbsoluteScheduleDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public AbsoluteScheduleDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public AbsoluteScheduleHollow getRecord(int ordinal){
        return api.getAbsoluteScheduleHollow(ordinal);
    }

}