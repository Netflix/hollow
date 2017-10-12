package com.netflix.hollow.core.type.accessor;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumerAPI;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.type.HBoolean;

public class BooleanDataAccessor extends AbstractHollowDataAccessor<HBoolean> {

    public static final String TYPE = "HBoolean";
    private HollowConsumerAPI.BooleanRetriever api;

    public BooleanDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (HollowConsumerAPI.BooleanRetriever)consumer.getAPI();
    }

    public BooleanDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.BooleanRetriever api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public BooleanDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.BooleanRetriever api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public BooleanDataAccessor(HollowReadStateEngine rStateEngine, HollowConsumerAPI.BooleanRetriever api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public HBoolean getRecord(int ordinal){
        return api.getHBoolean(ordinal);
    }

}