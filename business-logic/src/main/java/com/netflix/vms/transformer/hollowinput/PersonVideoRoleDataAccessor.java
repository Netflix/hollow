package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class PersonVideoRoleDataAccessor extends AbstractHollowDataAccessor<PersonVideoRoleHollow> {

    public static final String TYPE = "PersonVideoRoleHollow";
    private VMSHollowInputAPI api;

    public PersonVideoRoleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public PersonVideoRoleDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public PersonVideoRoleDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public PersonVideoRoleDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public PersonVideoRoleHollow getRecord(int ordinal){
        return api.getPersonVideoRoleHollow(ordinal);
    }

}