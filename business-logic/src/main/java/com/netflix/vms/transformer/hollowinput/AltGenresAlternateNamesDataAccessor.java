package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class AltGenresAlternateNamesDataAccessor extends AbstractHollowDataAccessor<AltGenresAlternateNamesHollow> {

    public static final String TYPE = "AltGenresAlternateNamesHollow";
    private VMSHollowInputAPI api;

    public AltGenresAlternateNamesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public AltGenresAlternateNamesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public AltGenresAlternateNamesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public AltGenresAlternateNamesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public AltGenresAlternateNamesHollow getRecord(int ordinal){
        return api.getAltGenresAlternateNamesHollow(ordinal);
    }

}