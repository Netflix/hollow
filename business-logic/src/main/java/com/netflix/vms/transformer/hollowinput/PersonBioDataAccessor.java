package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonBioDataAccessor extends AbstractHollowDataAccessor<PersonBioHollow> {

    public static final String TYPE = "PersonBioHollow";
    private VMSHollowInputAPI api;

    public PersonBioDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonBioDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonBioDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonBioDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonBioHollow getRecord(int ordinal){
        return api.getPersonBioHollow(ordinal);
    }

}