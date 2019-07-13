package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ISOCountryDataAccessor extends AbstractHollowDataAccessor<ISOCountry> {

    public static final String TYPE = "ISOCountry";
    private RolloutAPI api;

    public ISOCountryDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (RolloutAPI)consumer.getAPI();
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, RolloutAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public ISOCountry getRecord(int ordinal){
        return api.getISOCountry(ordinal);
    }

}