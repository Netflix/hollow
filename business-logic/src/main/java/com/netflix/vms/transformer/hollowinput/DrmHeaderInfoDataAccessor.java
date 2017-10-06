package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DrmHeaderInfoDataAccessor extends AbstractHollowDataAccessor<DrmHeaderInfoHollow> {

    public static final String TYPE = "DrmHeaderInfoHollow";
    private VMSHollowInputAPI api;

    public DrmHeaderInfoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DrmHeaderInfoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DrmHeaderInfoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DrmHeaderInfoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DrmHeaderInfoHollow getRecord(int ordinal){
        return api.getDrmHeaderInfoHollow(ordinal);
    }

}