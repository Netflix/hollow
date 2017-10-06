package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class OriginServerDataAccessor extends AbstractHollowDataAccessor<OriginServerHollow> {

    public static final String TYPE = "OriginServerHollow";
    private VMSHollowInputAPI api;

    public OriginServerDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public OriginServerDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public OriginServerDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public OriginServerDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public OriginServerHollow getRecord(int ordinal){
        return api.getOriginServerHollow(ordinal);
    }

}