package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamProfilesDataAccessor extends AbstractHollowDataAccessor<StreamProfilesHollow> {

    public static final String TYPE = "StreamProfilesHollow";
    private VMSHollowInputAPI api;

    public StreamProfilesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamProfilesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamProfilesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamProfilesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamProfilesHollow getRecord(int ordinal){
        return api.getStreamProfilesHollow(ordinal);
    }

}