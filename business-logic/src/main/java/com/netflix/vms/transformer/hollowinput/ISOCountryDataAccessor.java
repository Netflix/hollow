package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class ISOCountryDataAccessor extends AbstractHollowDataAccessor<ISOCountryHollow> {

    public static final String TYPE = "ISOCountryHollow";
    private VMSHollowInputAPI api;

    public ISOCountryDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public ISOCountryDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public ISOCountryHollow getRecord(int ordinal){
        return api.getISOCountryHollow(ordinal);
    }

}