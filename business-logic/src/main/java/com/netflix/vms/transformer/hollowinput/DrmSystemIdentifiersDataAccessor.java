package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DrmSystemIdentifiersDataAccessor extends AbstractHollowDataAccessor<DrmSystemIdentifiersHollow> {

    public static final String TYPE = "DrmSystemIdentifiersHollow";
    private VMSHollowInputAPI api;

    public DrmSystemIdentifiersDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DrmSystemIdentifiersDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DrmSystemIdentifiersDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DrmSystemIdentifiersDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DrmSystemIdentifiersHollow getRecord(int ordinal){
        return api.getDrmSystemIdentifiersHollow(ordinal);
    }

}