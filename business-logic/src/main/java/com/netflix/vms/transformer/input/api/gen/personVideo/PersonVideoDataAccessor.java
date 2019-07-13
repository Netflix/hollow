package com.netflix.vms.transformer.input.api.gen.personVideo;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class PersonVideoDataAccessor extends AbstractHollowDataAccessor<PersonVideo> {

    public static final String TYPE = "PersonVideo";
    private PersonVideoAPI api;

    public PersonVideoDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (PersonVideoAPI)consumer.getAPI();
    }

    public PersonVideoDataAccessor(HollowReadStateEngine rStateEngine, PersonVideoAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public PersonVideoDataAccessor(HollowReadStateEngine rStateEngine, PersonVideoAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public PersonVideoDataAccessor(HollowReadStateEngine rStateEngine, PersonVideoAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public PersonVideo getRecord(int ordinal){
        return api.getPersonVideo(ordinal);
    }

}