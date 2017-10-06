package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CertificationsDataAccessor extends AbstractHollowDataAccessor<CertificationsHollow> {

    public static final String TYPE = "CertificationsHollow";
    private VMSHollowInputAPI api;

    public CertificationsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CertificationsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CertificationsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CertificationsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CertificationsHollow getRecord(int ordinal){
        return api.getCertificationsHollow(ordinal);
    }

}