package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class StreamProfileGroupsDataAccessor extends AbstractHollowDataAccessor<StreamProfileGroupsHollow> {

    public static final String TYPE = "StreamProfileGroupsHollow";
    private VMSHollowInputAPI api;

    public StreamProfileGroupsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public StreamProfileGroupsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public StreamProfileGroupsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public StreamProfileGroupsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public StreamProfileGroupsHollow getRecord(int ordinal){
        return api.getStreamProfileGroupsHollow(ordinal);
    }

}