package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class OverrideScheduleDataAccessor extends AbstractHollowDataAccessor<OverrideScheduleHollow> {

    public static final String TYPE = "OverrideScheduleHollow";
    private VMSHollowInputAPI api;

    public OverrideScheduleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public OverrideScheduleDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public OverrideScheduleDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public OverrideScheduleDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public OverrideScheduleHollow getRecord(int ordinal){
        return api.getOverrideScheduleHollow(ordinal);
    }

}