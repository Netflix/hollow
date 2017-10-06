package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StringDataAccessor extends AbstractHollowDataAccessor<StringHollow> {

    public static final String TYPE = "StringHollow";
    private VMSHollowInputAPI api;

    public StringDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StringHollow getRecord(int ordinal){
        return api.getStringHollow(ordinal);
    }

}