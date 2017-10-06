package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PhaseTagDataAccessor extends AbstractHollowDataAccessor<PhaseTagHollow> {

    public static final String TYPE = "PhaseTagHollow";
    private VMSHollowInputAPI api;

    public PhaseTagDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PhaseTagDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PhaseTagDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PhaseTagDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PhaseTagHollow getRecord(int ordinal){
        return api.getPhaseTagHollow(ordinal);
    }

}