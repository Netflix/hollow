package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class StringDataAccessor extends AbstractHollowDataAccessor<HString> {

    public static final String TYPE = "String";
    private MovieAPI api;

    public StringDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MovieAPI)consumer.getAPI();
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, MovieAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, MovieAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public StringDataAccessor(HollowReadStateEngine rStateEngine, MovieAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HString getRecord(int ordinal){
        return api.getHString(ordinal);
    }

}