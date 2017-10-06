package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class IndividualSupplementalDataAccessor extends AbstractHollowDataAccessor<IndividualSupplementalHollow> {

    public static final String TYPE = "IndividualSupplementalHollow";
    private VMSHollowInputAPI api;

    public IndividualSupplementalDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public IndividualSupplementalDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public IndividualSupplementalDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public IndividualSupplementalDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public IndividualSupplementalHollow getRecord(int ordinal){
        return api.getIndividualSupplementalHollow(ordinal);
    }

}