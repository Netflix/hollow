package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonVideoAliasIdDataAccessor extends AbstractHollowDataAccessor<PersonVideoAliasIdHollow> {

    public static final String TYPE = "PersonVideoAliasIdHollow";
    private VMSHollowInputAPI api;

    public PersonVideoAliasIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonVideoAliasIdDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonVideoAliasIdDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonVideoAliasIdDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonVideoAliasIdHollow getRecord(int ordinal){
        return api.getPersonVideoAliasIdHollow(ordinal);
    }

}