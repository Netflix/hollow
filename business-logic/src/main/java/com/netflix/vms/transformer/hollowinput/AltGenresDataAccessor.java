package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class AltGenresDataAccessor extends AbstractHollowDataAccessor<AltGenresHollow> {

    public static final String TYPE = "AltGenresHollow";
    private VMSHollowInputAPI api;

    public AltGenresDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public AltGenresDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public AltGenresDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public AltGenresDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public AltGenresHollow getRecord(int ordinal){
        return api.getAltGenresHollow(ordinal);
    }

}