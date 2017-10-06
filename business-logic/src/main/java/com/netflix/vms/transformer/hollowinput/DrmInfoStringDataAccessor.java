package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class DrmInfoStringDataAccessor extends AbstractHollowDataAccessor<DrmInfoStringHollow> {

    public static final String TYPE = "DrmInfoStringHollow";
    private VMSHollowInputAPI api;

    public DrmInfoStringDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public DrmInfoStringDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public DrmInfoStringDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public DrmInfoStringDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public DrmInfoStringHollow getRecord(int ordinal){
        return api.getDrmInfoStringHollow(ordinal);
    }

}