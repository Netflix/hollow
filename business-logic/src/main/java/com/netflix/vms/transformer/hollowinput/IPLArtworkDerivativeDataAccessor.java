package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class IPLArtworkDerivativeDataAccessor extends AbstractHollowDataAccessor<IPLArtworkDerivativeHollow> {

    public static final String TYPE = "IPLArtworkDerivativeHollow";
    private VMSHollowInputAPI api;

    public IPLArtworkDerivativeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public IPLArtworkDerivativeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public IPLArtworkDerivativeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public IPLArtworkDerivativeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public IPLArtworkDerivativeHollow getRecord(int ordinal){
        return api.getIPLArtworkDerivativeHollow(ordinal);
    }

}