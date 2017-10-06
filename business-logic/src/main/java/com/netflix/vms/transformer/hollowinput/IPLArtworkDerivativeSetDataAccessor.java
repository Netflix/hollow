package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class IPLArtworkDerivativeSetDataAccessor extends AbstractHollowDataAccessor<IPLArtworkDerivativeSetHollow> {

    public static final String TYPE = "IPLArtworkDerivativeSetHollow";
    private VMSHollowInputAPI api;

    public IPLArtworkDerivativeSetDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public IPLArtworkDerivativeSetDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public IPLArtworkDerivativeSetDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public IPLArtworkDerivativeSetDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public IPLArtworkDerivativeSetHollow getRecord(int ordinal){
        return api.getIPLArtworkDerivativeSetHollow(ordinal);
    }

}