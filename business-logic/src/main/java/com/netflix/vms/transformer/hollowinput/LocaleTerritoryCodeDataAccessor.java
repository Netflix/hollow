package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class LocaleTerritoryCodeDataAccessor extends AbstractHollowDataAccessor<LocaleTerritoryCodeHollow> {

    public static final String TYPE = "LocaleTerritoryCodeHollow";
    private VMSHollowInputAPI api;

    public LocaleTerritoryCodeDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (VMSHollowInputAPI)consumer.getAPI();
    }

    public LocaleTerritoryCodeDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public LocaleTerritoryCodeDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public LocaleTerritoryCodeDataAccessor(HollowReadStateEngine rStateEngine, VMSHollowInputAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public LocaleTerritoryCodeHollow getRecord(int ordinal){
        return api.getLocaleTerritoryCodeHollow(ordinal);
    }

}