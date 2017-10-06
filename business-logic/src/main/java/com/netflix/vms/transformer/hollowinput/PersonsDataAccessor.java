package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonsDataAccessor extends AbstractHollowDataAccessor<PersonsHollow> {

    public static final String TYPE = "PersonsHollow";
    private VMSHollowInputAPI api;

    public PersonsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonsDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonsDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonsDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonsHollow getRecord(int ordinal){
        return api.getPersonsHollow(ordinal);
    }

}