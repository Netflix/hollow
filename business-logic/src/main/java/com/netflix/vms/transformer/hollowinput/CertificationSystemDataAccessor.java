package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CertificationSystemDataAccessor extends AbstractHollowDataAccessor<CertificationSystemHollow> {

    public static final String TYPE = "CertificationSystemHollow";
    private VMSHollowInputAPI api;

    public CertificationSystemDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CertificationSystemDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CertificationSystemDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CertificationSystemDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CertificationSystemHollow getRecord(int ordinal){
        return api.getCertificationSystemHollow(ordinal);
    }

}