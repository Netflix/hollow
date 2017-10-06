package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class TerritoryCountriesDataAccessor extends AbstractHollowDataAccessor<TerritoryCountriesHollow> {

    public static final String TYPE = "TerritoryCountriesHollow";
    private VMSHollowInputAPI api;

    public TerritoryCountriesDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public TerritoryCountriesDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public TerritoryCountriesDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public TerritoryCountriesDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public TerritoryCountriesHollow getRecord(int ordinal){
        return api.getTerritoryCountriesHollow(ordinal);
    }

}