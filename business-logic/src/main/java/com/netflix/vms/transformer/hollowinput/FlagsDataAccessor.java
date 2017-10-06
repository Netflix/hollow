package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class FlagsDataAccessor extends AbstractHollowDataAccessor<FlagsHollow> {

    public static final String TYPE = "FlagsHollow";
    private VMSHollowInputAPI api;

    public FlagsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public FlagsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public FlagsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public FlagsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public FlagsHollow getRecord(int ordinal){
        return api.getFlagsHollow(ordinal);
    }

}