package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ExplicitDateDataAccessor extends AbstractHollowDataAccessor<ExplicitDateHollow> {

    public static final String TYPE = "ExplicitDateHollow";
    private VMSHollowInputAPI api;

    public ExplicitDateDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ExplicitDateDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ExplicitDateDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ExplicitDateDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ExplicitDateHollow getRecord(int ordinal){
        return api.getExplicitDateHollow(ordinal);
    }

}