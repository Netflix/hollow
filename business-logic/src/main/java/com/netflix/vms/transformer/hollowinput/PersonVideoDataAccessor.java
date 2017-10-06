package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonVideoDataAccessor extends AbstractHollowDataAccessor<PersonVideoHollow> {

    public static final String TYPE = "PersonVideoHollow";
    private VMSHollowInputAPI api;

    public PersonVideoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonVideoDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonVideoDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonVideoDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonVideoHollow getRecord(int ordinal){
        return api.getPersonVideoHollow(ordinal);
    }

}