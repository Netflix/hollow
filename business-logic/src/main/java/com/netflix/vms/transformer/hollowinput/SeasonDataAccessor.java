package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class SeasonDataAccessor extends AbstractHollowDataAccessor<SeasonHollow> {

    public static final String TYPE = "SeasonHollow";
    private VMSHollowInputAPI api;

    public SeasonDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public SeasonDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public SeasonDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public SeasonDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public SeasonHollow getRecord(int ordinal){
        return api.getSeasonHollow(ordinal);
    }

}