package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class PersonVideoRoleDataAccessor extends AbstractHollowDataAccessor<PersonVideoRole> {

    public static final String TYPE = "PersonVideoRole";
    private PersonVideoAPI api;

    public PersonVideoRoleDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (PersonVideoAPI)consumer.getAPI();
    }

    public PersonVideoRoleDataAccessor(HollowReadStateEngine rStateEngine, PersonVideoAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public PersonVideoRoleDataAccessor(HollowReadStateEngine rStateEngine, PersonVideoAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public PersonVideoRoleDataAccessor(HollowReadStateEngine rStateEngine, PersonVideoAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public PersonVideoRole getRecord(int ordinal){
        return api.getPersonVideoRole(ordinal);
    }

}