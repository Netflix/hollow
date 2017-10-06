package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DateDataAccessor extends AbstractHollowDataAccessor<DateHollow> {

    public static final String TYPE = "DateHollow";
    private VMSHollowInputAPI api;

    public DateDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DateDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DateDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DateDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DateHollow getRecord(int ordinal){
        return api.getDateHollow(ordinal);
    }

}