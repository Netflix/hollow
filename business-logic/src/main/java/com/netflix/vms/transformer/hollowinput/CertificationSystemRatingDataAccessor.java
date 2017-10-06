package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CertificationSystemRatingDataAccessor extends AbstractHollowDataAccessor<CertificationSystemRatingHollow> {

    public static final String TYPE = "CertificationSystemRatingHollow";
    private VMSHollowInputAPI api;

    public CertificationSystemRatingDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CertificationSystemRatingDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CertificationSystemRatingDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CertificationSystemRatingDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CertificationSystemRatingHollow getRecord(int ordinal){
        return api.getCertificationSystemRatingHollow(ordinal);
    }

}