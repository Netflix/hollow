package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TimecodeAnnotationDataAccessor extends AbstractHollowDataAccessor<TimecodeAnnotationHollow> {

    public static final String TYPE = "TimecodeAnnotationHollow";
    private VMSHollowInputAPI api;

    public TimecodeAnnotationDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TimecodeAnnotationDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TimecodeAnnotationDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TimecodeAnnotationDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TimecodeAnnotationHollow getRecord(int ordinal){
        return api.getTimecodeAnnotationHollow(ordinal);
    }

}