package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TimecodedMomentAnnotationDataAccessor extends AbstractHollowDataAccessor<TimecodedMomentAnnotationHollow> {

    public static final String TYPE = "TimecodedMomentAnnotationHollow";
    private VMSHollowInputAPI api;

    public TimecodedMomentAnnotationDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TimecodedMomentAnnotationDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TimecodedMomentAnnotationDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TimecodedMomentAnnotationDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TimecodedMomentAnnotationHollow getRecord(int ordinal){
        return api.getTimecodedMomentAnnotationHollow(ordinal);
    }

}