package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class FestivalsDataAccessor extends AbstractHollowDataAccessor<FestivalsHollow> {

    public static final String TYPE = "FestivalsHollow";
    private VMSHollowInputAPI api;

    public FestivalsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public FestivalsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public FestivalsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public FestivalsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public FestivalsHollow getRecord(int ordinal){
        return api.getFestivalsHollow(ordinal);
    }

}