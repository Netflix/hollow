package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class LocaleTerritoryCodeDataAccessor extends AbstractHollowDataAccessor<LocaleTerritoryCodeHollow> {

    public static final String TYPE = "LocaleTerritoryCodeHollow";
    private VMSHollowInputAPI api;

    public LocaleTerritoryCodeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public LocaleTerritoryCodeDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public LocaleTerritoryCodeDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public LocaleTerritoryCodeDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public LocaleTerritoryCodeHollow getRecord(int ordinal){
        return api.getLocaleTerritoryCodeHollow(ordinal);
    }

}