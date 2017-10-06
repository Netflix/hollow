package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ShowCountryLabelDataAccessor extends AbstractHollowDataAccessor<ShowCountryLabelHollow> {

    public static final String TYPE = "ShowCountryLabelHollow";
    private VMSHollowInputAPI api;

    public ShowCountryLabelDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ShowCountryLabelDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ShowCountryLabelDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ShowCountryLabelDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ShowCountryLabelHollow getRecord(int ordinal){
        return api.getShowCountryLabelHollow(ordinal);
    }

}