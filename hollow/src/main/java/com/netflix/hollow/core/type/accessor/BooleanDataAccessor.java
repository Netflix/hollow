package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HBoolean;

public class BooleanDataAccessor extends AbstractHollowDataAccessor<Boolean> {

    public static final String TYPE = "Boolean";
    private HollowConsumerAPI.BooleanRetriever api;

    public BooleanDataAccessor(HollowConsumer consumer) {
        this(consumer.getStateEngine(), (HollowConsumerAPI.BooleanRetriever)consumer.getAPI());
    }

    public BooleanDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.BooleanRetriever api) {
        this(rStateEngine, api, "value");
    }

    public BooleanDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.BooleanRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public BooleanDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.BooleanRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Boolean getRecord(int ordinal){
        HBoolean val = api.getHBoolean(ordinal);
        return val == null ? null : val.getValueBoxed();
    }
}